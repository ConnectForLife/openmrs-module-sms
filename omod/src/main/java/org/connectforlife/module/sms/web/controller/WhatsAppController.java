/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.audit.SmsAuditService;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.handler.IncomingMessageDataBuilder;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.IncomingMessageService;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Status;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.IncomingMessageAccessor;
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

import java.net.HttpURLConnection;
import java.util.*;

/** Handles events triggered from whatApp {server}/openmrs/ws/whatsapp/{Config} */
@Api(value = "Handles events triggered from whatApp", tags = {"REST API to handle events triggered from whatApp"})
@Controller
@RequestMapping(value = "/whatsapp")
public class WhatsAppController extends RestController {

  private static final Log LOGGER = LogFactory.getLog(WhatsAppController.class);
  private static final String STATUSES = "statuses";
  private static final String MESSAGES = "messages";

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

  @ApiOperation(value = "Handles events triggered from whatApp", notes = "Handles events triggered from whatApp")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful handling events triggered from whatApp"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to handle events triggered from whatApp"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in handling events triggered from whatApp")})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @RequestMapping(value = "/{configName}", method = RequestMethod.POST)
  public void handle(@ApiParam(name = "configName", value = "The name of the configuration")
          @PathVariable String configName,
                     @ApiParam(name = "bodyParam", value = "The request body param")
                     @RequestBody Map<String, Object> bodyParam) {
    LOGGER.info(
        String.format("whatsapp  event - configName = %s, bodyParam = %s", configName, bodyParam));

    if (!configService.hasConfig(configName)) {
      String msg =
          String.format(
              "Received whatsapp  event for '%s' config but no matching config will try the default config",
              configName);
      LOGGER.error(msg);
      return;
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
                SmsDirection.INBOUND,
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
      Collection<Map<String, String>> statusList,
      Collection<Map<String, String>> incomingMessageList) {
    for (Map.Entry<String, Object> en : bodyParam.entrySet()) {
      if (STATUSES.equals(en.getKey()) && en.getValue() instanceof List) {
        List<Object> objList = Collections.singletonList(en.getValue());
        for (Object ele : objList) {
          HashMap<String, Object> obj = (HashMap<String, Object>) ele;
          statusList.add(getCombinedParams(null, obj));
        }
      }
      if (MESSAGES.equals(en.getKey()) && en.getValue() instanceof List) {
        List<Object> objList = Collections.singletonList(en.getValue());
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
