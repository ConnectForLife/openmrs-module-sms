package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.handler.IncomingMessageDataBuilder;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.IncomingMessageService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Status;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.api.util.IncomingMessageAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sms.api.audit.SmsDirection.INBOUND;

/** Handles events triggered from whatApp {server}/openmrs/ws/whatsapp/{Config} */
@Controller
@RequestMapping(value = "/whatsapp")
public class WhatsAppController extends RestController {

  private static final Log LOGGER = LogFactory.getLog(WhatsAppController.class);

  private SmsAuditService smsAuditService;
  private TemplateService templateService;
  private ConfigService configService;
  private IncomingMessageService incomingMessageService;

  @Autowired
  public WhatsAppController(
      @Qualifier("templateService") TemplateService templateService,
      @Qualifier("sms.configService") ConfigService configService,
      @Qualifier("sms.SmsAuditService") SmsAuditService smsAuditService,
      IncomingMessageService incomingMessageService) {
    this.templateService = templateService;
    this.configService = configService;
    this.smsAuditService = smsAuditService;
    this.incomingMessageService = incomingMessageService;
  }

  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @RequestMapping(value = "/{configName}", method = RequestMethod.POST)
  public void handle(@PathVariable String configName, @RequestBody Map<String, Object> bodyParam) {
    LOGGER.info(
        String.format("whatsapp  event - configName = %s, bodyParam = %s", configName, bodyParam));

    if (!configService.hasConfig(configName)) {
      String msg =
          String.format(
              "Received whatsapp  event for '%s' config but no matching config will try the default config",
              configName);
      LOGGER.error(msg);
    }

    Config config = configService.getConfigOrDefault(configName);
    Template template = templateService.getTemplate(config.getTemplateName());
    Status status = template.getStatus();

    List<Map<String, String>> statusList = new ArrayList<>();
    List<Map<String, String>> incomingMessageList = new ArrayList<>();

    buildParams(bodyParam, statusList, incomingMessageList);
    updateStatuses(configName, status, statusList);

    if (!incomingMessageList.isEmpty()) {
      LOGGER.info(
          String.format(
              "whatsapp  event - incoming message count = %s", incomingMessageList.size()));

      for (Map<String, String> incomingMap : incomingMessageList) {
        LOGGER.info(String.format("whatsapp  event - incoming message data = %s", incomingMap));

        final IncomingMessageAccessor messageAccessor =
            new IncomingMessageAccessor(template, incomingMap);
        final SmsRecord smsRecord =
            new SmsRecord(
                config.getName(),
                INBOUND,
                messageAccessor.getSender(),
                messageAccessor.getMessage(),
                DateUtil.now(),
                messageAccessor.getStatus(),
                null,
                null,
                messageAccessor.getMsgId(),
                null);

        LOGGER.info(String.format("whatsapp  event - incoming message data = %s", smsRecord));
        smsAuditService.createOrUpdate(smsRecord);

        final SmsRecord savedSmsRecord = smsAuditService.createOrUpdate(smsRecord);
        incomingMessageService.handleMessageSafe(
            new IncomingMessageDataBuilder(messageAccessor)
                .setReceivedForAFistTime(savedSmsRecord != null)
                .setReceivedAt(smsRecord.getTimestamp())
                .setConfig(config)
                .build());
      }
    }
  }

  private void buildParams(
      @RequestBody Map<String, Object> bodyParam,
      List<Map<String, String>> statusList,
      List<Map<String, String>> incomingMessageList) {
    for (Map.Entry<String, Object> en : bodyParam.entrySet()) {
      if (en.getKey().equals("statuses") && en.getValue() instanceof List) {
        List<Object> objList = (List) en.getValue();
        for (Object ele : objList) {
          HashMap<String, Object> obj = (HashMap<String, Object>) ele;
          statusList.add(getCombinedParams(null, obj));
        }
      }
      if (en.getKey().equals("messages") && en.getValue() instanceof List) {
        List<Object> objList = (List) en.getValue();
        for (Object ele : objList) {
          HashMap<String, Object> obj = (HashMap<String, Object>) ele;
          incomingMessageList.add(getCombinedParams(null, obj));
        }
      }
    }
  }

  private void updateStatuses(
      String configName, Status status, List<Map<String, String>> statusList) {
    if (status.hasMessageIdKey() && !statusList.isEmpty()) {
      for (Map<String, String> statusMap : statusList) {
        if (statusMap != null && statusMap.containsKey(status.getMessageIdKey())) {
          if (status.hasStatusKey() && status.hasStatusSuccess()) {
            smsAuditService.createOrUpdate(status, configName, statusMap);
          } else {
            String msg =
                String.format(
                    "We have a message id, but don't know how to extract message status, this is most likely a template error. Config: %s, Parameters: %s",
                    configName, statusMap);
            LOGGER.error(msg);
          }
        } else {
          String msg =
              String.format(
                  "Status message received from provider, but no template support! Config: %s, Parameters: %s",
                  configName, statusMap);
          LOGGER.error(msg);
        }
      }
    }
  }
}
