/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.event.SmsEvent;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.service.SmsEventService;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Response;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.util.SmsEventsHelper;
import org.connectforlife.module.sms.api.util.TemplatePropertiesBuilder;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** This is the main meat - here we talk to the providers using HTTP. */
public class SmsHttpService {

  private static final String SMS_MODULE = "openmrs-sms";
  private static final Log LOGGER = LogFactory.getLog(SmsHttpService.class);

  private HttpClientService httpClientService;
  private HttpAuthenticationService httpAuthenticationService;
  private TemplateService templateService;
  private ConfigService configService;
  private SmsEventService smsEventService;
  private SmsRecordDao smsRecordDao;

  /**
   * This method allows sending outgoing sms messages through HTTP. The configuration specified in
   * the {@link OutgoingSms} object will be used for dealing with the provider.
   *
   * @param sms the representation of the sms to send
   */
  @Transactional
  public synchronized void send(OutgoingSms sms) {
    final Config config = configService.getConfigOrDefault(sms.getConfig());
    final Template template = templateService.getTemplate(config.getTemplateName());

    try {
      final HttpMethod sendSMSMethod = prepHttpMethod(sms, template, config);
      final SendSmsState sendSmsState =
          new SendSmsState()
              .setHttpClient(httpClientService.getProviderHttpClient(config))
              .setTemplate(template)
              .setHttpMethod(sendSMSMethod)
              .doRequest();

      handleSendSMSResponse(config, sms, sendSmsState);
    } catch (Exception e) {
      LOGGER.error(
          MessageFormat.format(
              "Failed to make SMS HTTP request for config {0} and sms id: {1}",
              config.getName(), sms.getOpenMrsId()),
          e);

      for (String recipient : sms.getRecipients()) {
        smsRecordDao.createOrUpdate(
            new SmsRecord(
                config.getName(),
                SmsDirection.OUTBOUND,
                recipient,
                sms.getMessage(),
                DateUtil.now(),
                DeliveryStatusesConstants.ABORTED,
                null,
                sms.getOpenMrsId(),
                null,
                e.getMessage()));
      }
    }
  }

  private void handleSendSMSResponse(Config config, OutgoingSms sms, SendSmsState sendSmsState) {
    Integer httpStatus = sendSmsState.getHttpStatus();
    String httpResponse = sendSmsState.getHttpResponse();
    String errorMessage = sendSmsState.getErrorMessage();

    //
    // make sure we don't talk to the SMS provider too fast (some only allow a max of n per minute
    // calls)
    //
    delayProviderAccess(sendSmsState.getTemplate());

    List<SmsEvent> events = new ArrayList<>();
    List<SmsRecord> auditRecords = new ArrayList<>();
    Integer failureCount = sms.getFailureCount();

    //
    // Analyze provider's response
    //
    Response templateResponse = sendSmsState.getTemplate().getOutgoing().getResponse();
    if (httpStatus == null
        || !templateResponse.isSuccessStatus(httpStatus)
        || sendSmsState.getHttpMethod() == null) {
      //
      // HTTP Request Failure
      //
      failureCount++;
      handleFailure(
          httpStatus,
          errorMessage,
          failureCount,
          templateResponse,
          httpResponse,
          config,
          sms,
          auditRecords,
          events);
    } else {
      //
      // HTTP Request Success, now look more closely at what the provider is telling us
      //
      ResponseHandler handler =
          createResponseHandler(sendSmsState.getTemplate(), templateResponse, config, sms);

      try {
        handler.handle(sms, httpResponse, sendSmsState.getHttpMethod().getResponseHeaders());
      } catch (IllegalStateException | IllegalArgumentException e) {
        // exceptions generated above should only come from config/template issues, try to display
        // something useful in the openmrs messages and tomcat log
        throw e;
      }
      events = handler.getEvents();
      auditRecords = handler.getAuditRecords();
    }

    //
    // Finally send all the events that need sending...
    //
    for (SmsEvent event : events) {
      smsEventService.sendEventMessage(event);
    }

    //
    // ...and audit all the records that need auditing
    //
    for (SmsRecord smsRecord : auditRecords) {
      smsRecordDao.createOrUpdate(smsRecord);
    }
  }

  private static String printableMethodParams(HttpMethod method) {
    if (method.getClass().equals(PostMethod.class)) {
      PostMethod postMethod = (PostMethod) method;
      RequestEntity requestEntity = postMethod.getRequestEntity();
      if (MediaType.APPLICATION_FORM_URLENCODED.equals(requestEntity.getContentType())) {
        StringBuilder sb = new StringBuilder();
        NameValuePair[] params = postMethod.getParameters();
        for (NameValuePair param : params) {
          if (sb.length() > 0) {
            sb.append(", ");
          }
          sb.append(String.format("%s: %s", param.getName(), param.getValue()));
        }
        return "POST Parameters: " + sb;
      } else if (requestEntity.getClass() == StringRequestEntity.class) {
        // Assume MediaType.APPLICATION_JSON_VALUE
        return "POST JSON: " + ((StringRequestEntity) requestEntity).getContent();
      }
    } else if (method.getClass().equals(GetMethod.class)) {
      GetMethod g = (GetMethod) method;
      return String.format("GET QueryString: %s", g.getQueryString());
    }

    throw new IllegalStateException(String.format("Unexpected HTTP method: %s", method.getClass()));
  }

  private void delayProviderAccess(Template template) {
    // todo: serialize access to configs, ie: one provider may allow 100 sms/min and another may
    // allow 10...
    // This prevents us from sending more messages per second than the provider allows
    Integer milliseconds = template.getOutgoing().getMillisecondsBetweenMessages();
    sleep(milliseconds);
  }

  // CHECKSTYLE:OFF: ParameterNumber
  private void handleFailure(
      Integer httpStatus,
      String priorErrorMessage, // NO CHECKSTYLE ParameterNumber
      Integer failureCount,
      Response templateResponse,
      String httpResponse,
      Config config,
      OutgoingSms sms,
      List<SmsRecord> auditRecords,
      List<SmsEvent> events) {
    String errorMessage = priorErrorMessage;

    if (httpStatus == null) {
      String msg = String.format("Delivery to SMS provider failed: %s", errorMessage);
      LOGGER.error(msg);
    } else {
      errorMessage = templateResponse.extractGeneralFailureMessage(httpResponse);
      if (errorMessage == null) {
        LOGGER.error(
            String.format(
                "%s - Unable to extract failure message for '%s' config: %s",
                config.getName(), httpResponse, SMS_MODULE));
        errorMessage = httpResponse;
      }
      LOGGER.error(
          String.format(
              "Delivery to SMS provider failed with HTTP %d: %s", httpStatus, errorMessage));
    }

    for (String recipient : sms.getRecipients()) {
      auditRecords.add(
          new SmsRecord(
              config.getName(),
              SmsDirection.OUTBOUND,
              recipient,
              sms.getMessage(),
              DateUtil.now(),
              config.retryOrAbortStatus(failureCount),
              null,
              sms.getOpenMrsId(),
              null,
              errorMessage));
    }

    events.add(
        SmsEventsHelper.outboundEvent(
            config.retryOrAbortSubject(failureCount),
            config.getName(),
            sms.getRecipients(),
            sms.getMessage(),
            sms.getOpenMrsId(),
            null,
            sms.getFailureCount() + 1,
            null,
            null,
            sms.getCustomParams()));
  }
  // CHECKSTYLE:ON: ParameterNumber

  private ResponseHandler createResponseHandler(
      Template template, Response templateResponse, Config config, OutgoingSms sms) {
    ResponseHandler handler;
    if (templateResponse.supportsSingleRecipientResponse().equals(Boolean.TRUE)) {
      if (sms.getRecipients().size() == 1) {
        handler = new MultilineSingleResponseHandler(template, config);
      } else {
        handler = new MultilineResponseHandler(template, config);
      }
    } else {
      handler = new GenericResponseHandler(template, config);
    }

    return handler;
  }

  private HttpMethod prepHttpMethod(OutgoingSms sms, Template template, Config config) {
    final Map<String, Object> props =
        new TemplatePropertiesBuilder()
            .withConfigService(configService)
            .withTemplate(template)
            .withConfig(config)
            .withOutgoingSms(sms)
            .build();
    final HttpMethod method = template.generateRequestFor(props);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(printableMethodParams(method));
    }

    httpAuthenticationService.authenticate(
        new HttpAuthenticationContext(
            httpClientService.getProviderHttpClient(config), method, config, template, props));

    return method;
  }

  private void sleep(long milliseconds) {
    LOGGER.debug(
        String.format(
            "Sleeping thread id %d for %d ms", Thread.currentThread().getId(), milliseconds));
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  public synchronized void setHttpClientService(HttpClientService httpClientService) {
    this.httpClientService = httpClientService;
  }

  public synchronized void setHttpAuthenticationService(
      HttpAuthenticationService httpAuthenticationService) {
    this.httpAuthenticationService = httpAuthenticationService;
  }

  public synchronized void setTemplateService(TemplateService templateService) {
    this.templateService = templateService;
  }

  public synchronized void setSmsEventService(SmsEventService smsEventService) {
    this.smsEventService = smsEventService;
  }

  public synchronized void setConfigService(ConfigService configService) {
    this.configService = configService;
  }

  public synchronized void setSmsRecordDao(SmsRecordDao smsRecordDao) {
    this.smsRecordDao = smsRecordDao;
  }
}
