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
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Status;
import org.connectforlife.module.sms.api.templates.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Handles message delivery status updates sent by sms providers to
 * {server}/openmrs/ws/sms/status{Config}
 */
@Api(value = "Handles message delivery status updates sent by SMS providers",
        tags = {"REST API to handle message delivery status updates sent by SMS providers"})
@Controller
@RequestMapping(value = "/sms/status")
public class StatusController extends RestController {

  private static final Log LOGGER = LogFactory.getLog(StatusController.class);

  private SmsAuditService smsAuditService;
  private TemplateService templateService;
  private ConfigService configService;

  @Autowired
  public StatusController(
      @Qualifier("templateService") TemplateService templateService,
      @Qualifier("sms.configService") ConfigService configService,
      @Qualifier("sms.SmsAuditService") SmsAuditService smsAuditService) {
    this.templateService = templateService;
    this.configService = configService;
    this.smsAuditService = smsAuditService;
  }

  /**
   * Handles a status update from a provider. This method will result in publishing a OpenMRS Event
   * and creating a record in the database.
   *
   * @param configName the name of the configuration for the provider that is sending the update
   * @param params params of the request sent by the provider
   */
  @ApiOperation(
      value = "Publishing an OpenMRS Event and creating a record in the database",
      notes = "Publishing an OpenMRS Event and creating a record in the database")
  @ApiResponses(value = {
        @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful publishing an OpenMRS Event"),
        @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to publish an OpenMRS Event"),
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in publishing an OpenMRS Event")})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @RequestMapping(value = "/{configName}", method = RequestMethod.POST)
  public void handle(
          @ApiParam(name = "configName", value = "The name of the configuration for the provider that is sending the update")
      @PathVariable String configName,
          @ApiParam(name = "params", value = "Params of the request sent by the provider")
      @RequestParam Map<String, String> params,
          @ApiParam(name = "bodyParam", value = "The request body param")
      @RequestBody Map<String, Object> bodyParam) {
    LOGGER.info(
        String.format(
            "SMS Status - configName = %s, params = %s, bodyParam = %s",
            configName, params, bodyParam));

    if (!configService.hasConfig(configName)) {
      String msg =
          String.format(
              "Received SMS Status for '%s' config but no matching config: %s, "
                  + "will try the default config",
              configName, params);
      LOGGER.error(msg);
    }
    Config config = configService.getConfigOrDefault(configName);
    Template template = templateService.getTemplate(config.getTemplateName());
    Status status = template.getStatus();
    Map<String, String> combinedParams = getCombinedParams(params, bodyParam);

    if (status.hasMessageIdKey()
        && combinedParams != null
        && combinedParams.containsKey(status.getMessageIdKey())) {
      if (status.hasStatusKey() && status.hasStatusSuccess()) {
        smsAuditService.createOrUpdate(status, configName, combinedParams);
      } else {
        String msg =
            String.format(
                "We have a message id, but don't know how to extract message status, this is most likely a template error. Config: %s, Parameters: %s",
                configName, combinedParams);
        LOGGER.error(msg);
      }
    } else {
      String msg =
          String.format(
              "Status message received from provider, but no template support! Config: %s, Parameters: %s",
              configName, combinedParams);
      LOGGER.error(msg);
    }
  }
}
