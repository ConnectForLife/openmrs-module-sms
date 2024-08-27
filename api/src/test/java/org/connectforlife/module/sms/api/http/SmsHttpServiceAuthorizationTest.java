/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 *
 */

package org.connectforlife.module.sms.api.http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.connectforlife.module.sms.builder.OutgoingSmsBuilder;
import org.connectforlife.module.sms.mock.MockHttpClient;
import org.connectforlife.module.sms.util.ResourceUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.ContextSensitiveWithActivatorTest;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Template;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Pattern;

public class SmsHttpServiceAuthorizationTest extends ContextSensitiveWithActivatorTest {
  private final String authorizationHeaderValue =
      "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
  private static final String AUTH_RESPONSE =
      "{\n"
          + "  \"success\": true,\n"
          + "  \"expires_at\": \"2019-01-01T00:00:00Z\",\n"
          + "  \"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\",\n"
          + "  \"refresh_token\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE1NzA2OTgxODMsImV4cCI6MTYwMjIzNDE4MywiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3\"\n"
          + "}";

  private static final String PROVIDER_ID = "3e381e5d-3dfe-4b27-99d9-6e6f8bcdce30";
  private static final String SEND_SMS_RESPONSE =
      "{\n"
          + "  \"success\": true,\n"
          + "  \"message\": \"Message successfully queued\",\n"
          + "  \"cost\": 1,\n"
          + "  \"msgRef\": \"30bb083a-ae95-43b9-8ed5-051693d018af\",\n"
          + "  \"gatewayRef\": \"3e381e5d-3dfe-4b27-99d9-6e6f8bcdce30\"\n"
          + "}";

  @Autowired private TemplateService templateService;
  @Autowired private ConfigService configService;
  @Autowired private SmsHttpService smsHttpService;
  @Autowired private SingletonHttpClientService httpClientService;
  @Autowired private SmsRecordDao smsRecordDao;

  private final MockHttpClient smsHttpClient = new MockHttpClient();

  @Before
  public void setup() throws IOException {
    final Template fdiTemplate = ResourceUtil.loadJSON("templates/fdi-template.json", Template.class);
    templateService.importTemplate(fdiTemplate);

    final Configs configs = ResourceUtil.loadJSON("configs/fdi-config.json", Configs.class);
    configService.updateConfigs(configs);

    httpClientService.setSingleton(smsHttpClient);

    smsHttpClient.addResponse(Pattern.compile("/api/v1/auth.*"), HttpStatus.SC_OK, AUTH_RESPONSE);
    smsHttpClient.addResponse(
        Pattern.compile("/api/v1/mt/single.*"), HttpStatus.SC_OK, SEND_SMS_RESPONSE);
  }

  @After
  public void cleanDatabase() throws Exception {
    this.deleteAllData();
  }

  @Test
  public void testJWTAuthorization() {
    final String message = "Test message";
    final String recipient = "01600123321";
    final OutgoingSms outgoingSms =
        new OutgoingSmsBuilder()
            .withConfig(configService.getDefaultConfig().getName())
            .withMessage(message)
            .withRecipients(Collections.singletonList(recipient))
            .build();
    outgoingSms.getCustomParams().put("openMrsId", UUID.randomUUID().toString());

    smsHttpService.send(outgoingSms);

    final HttpMethod sendSmsMethod = smsHttpClient.getExecutedMethods().peekLast();
    Assert.assertNotNull(sendSmsMethod);
    Assert.assertEquals(
        authorizationHeaderValue,
        sendSmsMethod.getRequestHeader(HttpHeaders.AUTHORIZATION).getValue());

    final SmsRecord smsRecord = smsRecordDao.retrieveAll().get(0);
    Assert.assertEquals(recipient, smsRecord.getPhoneNumber());
    Assert.assertEquals(message, smsRecord.getMessageContent());
    Assert.assertEquals(DeliveryStatusesConstants.DISPATCHED, smsRecord.getDeliveryStatus());
    Assert.assertEquals(PROVIDER_ID, smsRecord.getProviderId());
  }
}
