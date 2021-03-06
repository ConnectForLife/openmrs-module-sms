package org.openmrs.module.sms.api.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.ConfigProp;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.service.SmsEventService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Response;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.DateUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sms.api.audit.SmsDirection.OUTBOUND;
import static org.openmrs.module.sms.api.util.SmsEventsHelper.outboundEvent;

/** This is the main meat - here we talk to the providers using HTTP. */
public class SmsHttpService {

  private static final String SMS_MODULE = "openmrs-sms";
  private static final Log LOGGER = LogFactory.getLog(SmsHttpService.class);
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

  private final HttpClient commonsHttpClient;

  private TemplateService templateService;
  private ConfigService configService;
  private SmsEventService smsEventService;
  private SmsRecordDao smsRecordDao;

  /**
   * The package-private constructor which <b>facilitates unit-tests</b> and allows usage of
   * custom/mock HttpClient object.
   *
   * @param commonsHttpClient the Http Client to use, not null
   */
  SmsHttpService(HttpClient commonsHttpClient) {
    this.commonsHttpClient = commonsHttpClient;
  }

  /** The default constructor, used by Spring to initialize this bean. */
  public SmsHttpService() {
    this(new HttpClient(new MultiThreadedHttpConnectionManager()));
  }

  /**
   * This method allows sending outgoing sms messages through HTTP. The configuration specified in
   * the {@link OutgoingSms} object will be used for dealing with the provider.
   *
   * @param sms the representation of the sms to send
   */
  @Transactional
  public synchronized void send(OutgoingSms sms) {

    Config config = configService.getConfigOrDefault(sms.getConfig());
    Template template = templateService.getTemplate(config.getTemplateName());
    HttpMethod httpMethod = null;
    Integer failureCount = sms.getFailureCount();
    Integer httpStatus = null;
    String httpResponse = null;
    String errorMessage = null;
    Map<String, Object> props = generateProps(sms, template, config);
    List<SmsEvent> events = new ArrayList<>();
    List<SmsRecord> auditRecords = new ArrayList<>();

    //
    // Generate the HTTP request
    //
    SendSmsState sendSmsState =
        new SendSmsState()
            .setCommonsHttpClient(commonsHttpClient)
            .setTemplate(template)
            .setHttpMethod(prepHttpMethod(template, props, config))
            .setHttpStatus(httpStatus)
            .setHttpResponse(httpResponse)
            .setErrorMessage(errorMessage)
            .build();
    httpMethod = sendSmsState.getHttpMethod();
    httpStatus = sendSmsState.getHttpStatus();
    httpResponse = sendSmsState.getHttpResponse();
    errorMessage = sendSmsState.getErrorMessage();

    //
    // make sure we don't talk to the SMS provider too fast (some only allow a max of n per minute
    // calls)
    //
    delayProviderAccess(template);

    //
    // Analyze provider's response
    //
    Response templateResponse = template.getOutgoing().getResponse();
    if (httpStatus == null || !templateResponse.isSuccessStatus(httpStatus) || httpMethod == null) {
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
      ResponseHandler handler = createResponseHandler(template, templateResponse, config, sms);

      try {
        handler.handle(sms, httpResponse, httpMethod.getResponseHeaders());
      } catch (IllegalStateException | IllegalArgumentException e) {
        // exceptions generated above should only come from config/template issues, try to display
        // something
        // useful in the openmrs messages and tomcat log
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
        return "POST Parameters: " + sb.toString();
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

  private void authenticate(Map<String, Object> props, Config config) {
    if (props.containsKey(USERNAME) && props.containsKey(PASSWORD)) {
      String u = props.get(USERNAME).toString();
      String p = props.get(PASSWORD).toString();
      commonsHttpClient.getParams().setAuthenticationPreemptive(true);
      commonsHttpClient
          .getState()
          .setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(u, p));
    } else {
      String message;
      if (props.containsKey(USERNAME)) {
        message = String.format("Config %s: missing password", config.getName());
      } else if (props.containsKey(PASSWORD)) {
        message = String.format("Config %s: missing username", config.getName());
      } else {
        message = String.format("Config %s: missing username and password", config.getName());
      }
      throw new IllegalStateException(message);
    }
  }

  private void delayProviderAccess(Template template) {
    // todo: serialize access to configs, ie: one provider may allow 100 sms/min and another may
    // allow 10...
    // This prevents us from sending more messages per second than the provider allows
    Integer milliseconds = template.getOutgoing().getMillisecondsBetweenMessages();
    sleep(milliseconds);
  }

  private Map<String, Object> generateProps(OutgoingSms sms, Template template, Config config) {
    Map<String, Object> props = new HashMap<>();
    props.put("recipients", template.recipientsAsString(sms.getRecipients()));
    props.put("message", escapeForJson(sms.getMessage()));
    props.put("openMrsId", sms.getOpenMrsId());
    props.put("callback", configService.getServerUrl() + "/ws/sms/status/" + config.getName());
    if (sms.getCustomParams() != null) {
      props.putAll(sms.getCustomParams());
    }

    for (ConfigProp configProp : config.getProps()) {
      props.put(configProp.getName(), configProp.getValue());
    }

    // ***** WARNING *****
    // This displays usernames & passwords in the server log! But then again, so does the settings
    // UI...
    // ***** WARNING *****
    if (LOGGER.isDebugEnabled()) {
      for (Map.Entry<String, Object> entry : props.entrySet()) {
        LOGGER.debug(String.format("PROP %s: %s", entry.getKey(), entry.getValue()));
      }
    }

    return props;
  }

  private String escapeForJson(String text) {
    return text.replace("\\", "\\\\").replace("\"", "\\\"");
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
              OUTBOUND,
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
        outboundEvent(
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
    if (templateResponse.supportsSingleRecipientResponse()) {
      if (sms.getRecipients().size() == 1 && templateResponse.supportsSingleRecipientResponse()) {
        handler = new MultilineSingleResponseHandler(template, config);
      } else {
        handler = new MultilineResponseHandler(template, config);
      }
    } else {
      handler = new GenericResponseHandler(template, config);
    }

    return handler;
  }

  private HttpMethod prepHttpMethod(Template template, Map<String, Object> props, Config config) {
    HttpMethod method = template.generateRequestFor(props);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(printableMethodParams(method));
    }

    if (template.getOutgoing().hasAuthentication()) {
      authenticate(props, config);
    }
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

  public void setTemplateService(TemplateService templateService) {
    this.templateService = templateService;
  }

  public void setSmsEventService(SmsEventService smsEventService) {
    this.smsEventService = smsEventService;
  }

  public void setConfigService(ConfigService configService) {
    this.configService = configService;
  }

  public void setSmsRecordDao(SmsRecordDao smsRecordDao) {
    this.smsRecordDao = smsRecordDao;
  }
}
