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

import org.connectforlife.module.sms.builder.ConfigsBuilder;
import org.connectforlife.module.sms.builder.OutgoingSmsBuilder;
import org.connectforlife.module.sms.builder.SmsRecordBuilder;
import org.connectforlife.module.sms.mock.SmsHttpClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.ContextSensitiveWithActivatorTest;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.SmsRecordSearchCriteria;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SmsHttpServiceTest extends ContextSensitiveWithActivatorTest {
  private static final String SUCCESS_MESSAGE =
      "{\n"
          + "      \"to\": \"447700900000\",\n"
          + "      \"status\": \"0\",\n"
          + "      \"remaining-balance\": \"3.14159265\",\n"
          + "      \"message-price\": \"0.03330000\",\n"
          + "      \"network\": \"12345\",\n"
          + "      \"client-ref\": \"my-personal-reference\",\n"
          + "      \"account-ref\": \"customer1234\"\n"
          + "      %s"
          + "    }";

  private static final String ERROR_MESSAGE =
      "{\n"
          + "    \"message-count\": \"1\",\n"
          + "    \"messages\": [{\n"
          + "        \"status\": \"4\",\n"
          + "        \"error-text\": \"Bad Credentials\"\n"
          + "    }]\n"
          + "}";

  private static final String ERROR_WITHOUT_MESSAGE_ID =
      "{\n      \"to\": \"447700900000\",\n      "
          + "\"status\": \"0\",\n      \"remaining-balance\": \"3.14159265\",\n      \"message-price\": "
          + "\"0.03330000\",\n      \"network\": \"12345\",\n      \"client-ref\": \"my-personal-reference\",\n "
          + "     \"account-ref\": \"customer1234\"\n          }";
  private static final String NEXMO_GENERIC = "nexmo-generic";
  private static final String BAD_CREDENTIALS = "Bad Credentials";

  @Autowired
  @Qualifier("templateService")
  private TemplateService templateService;

  @Autowired
  @Qualifier("sms.configService")
  private ConfigService configService;

  @Autowired
  @Qualifier("sms.SmsRecordDao")
  private SmsRecordDao smsRecordDao;

  @Autowired private SingletonHttpClientService httpClientService;

  @Autowired private SmsHttpService smsHttpService;

  @Before
  public void setUp() {
    smsRecordDao.deleteAll();
    Configs configs = new ConfigsBuilder().buildAsNew();
    configService.updateConfigs(configs);
    templateService.loadTemplates();
  }

  @After
  public void cleanDatabase() throws Exception {
    smsRecordDao.deleteAll();
    this.deleteAllData();
  }

  @Test
  public void sendProcessWithInvalidAuthenticateParams() {
    prepareSuccessAnswer(true);
    OutgoingSms sms = new OutgoingSmsBuilder().withWithAuthenticationParams(false).build();
    smsHttpService.send(sms);
    SmsRecord actual = smsRecordDao.getAll(true).get(0);
    Assert.assertEquals("Config nexmo: missing username and password", actual.getErrorMessage());
  }

  @Test
  public void sendProcessWithSuccessMessage() {
    String message = UUID.randomUUID().toString();
    prepareSuccessAnswer(true);
    OutgoingSms sms = new OutgoingSmsBuilder().withMessage(message).build();
    smsHttpService.send(sms);
    SmsRecord expected =
        new SmsRecordBuilder().withMessageContent(message).withOpenMrsId(null).build();
    SmsRecord actual =
        smsRecordDao
            .findByCriteria(new SmsRecordSearchCriteria().withMessageContent(message))
            .get(0);
    assertSmsRecord(expected, actual);
  }

  @Test
  public void sendProcessWithSuccessStatusWithoutMessageId() {
    prepareSuccessAnswer(false);
    OutgoingSms sms = new OutgoingSmsBuilder().build();
    smsHttpService.send(sms);
    SmsRecord expected =
        new SmsRecordBuilder()
            .withOpenMrsId(null)
            .withProviderId(null)
            .withErrorMessage(ERROR_WITHOUT_MESSAGE_ID)
            .withDeliveryStatus(DeliveryStatusesConstants.ABORTED)
            .build();
    SmsRecord actual = smsRecordDao.getAll(true).get(0);
    assertSmsRecord(expected, actual);
  }

  @Test
  public void sendProcessWithGeneralFailureMessage() {
    prepareFailureAnswer();
    OutgoingSms sms = new OutgoingSmsBuilder().build();
    smsHttpService.send(sms);
    SmsRecord expected =
        new SmsRecordBuilder()
            .withOpenMrsId(null)
            .withProviderId(null)
            .withProviderStatus(null)
            .withErrorMessage(BAD_CREDENTIALS)
            .withDeliveryStatus(DeliveryStatusesConstants.ABORTED)
            .build();
    SmsRecord actual = smsRecordDao.getAll(true).get(0);
    assertSmsRecord(expected, actual);
  }

  @Test
  public void sendSuccessMessageWithGenericHandler() {
    String message = UUID.randomUUID().toString();
    prepareSuccessAnswer(true);
    OutgoingSms sms =
        new OutgoingSmsBuilder().withConfig(NEXMO_GENERIC).withMessage(message).build();
    smsHttpService.send(sms);
    SmsRecord expected =
        new SmsRecordBuilder()
            .withConfig(NEXMO_GENERIC)
            .withMessageContent(message)
            .withProviderStatus(null)
            .withOpenMrsId(null)
            .build();
    SmsRecord actual =
        smsRecordDao
            .findByCriteria(new SmsRecordSearchCriteria().withMessageContent(message))
            .get(0);
    assertSmsRecord(expected, actual);
  }

  private void prepareSuccessAnswer(boolean withMessageId) {
    httpClientService.setSingleton(
        new SmsHttpClient()
            .withSuccessProcessed(true, SUCCESS_MESSAGE)
            .withMessageId(withMessageId));
  }

  private void prepareFailureAnswer() {
    httpClientService.setSingleton(new SmsHttpClient().withSuccessProcessed(false, ERROR_MESSAGE));
  }

  private void assertSmsRecord(SmsRecord expected, SmsRecord actual) {
    assertThat(actual.getConfig(), is(expected.getConfig()));
    assertThat(actual.getSmsDirection(), is(expected.getSmsDirection()));
    assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
    assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
    assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
    assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
    assertThat(actual.getOpenMrsId(), is(expected.getOpenMrsId()));
    assertThat(actual.getProviderId(), is(expected.getProviderId()));
    assertThat(actual.getErrorMessage(), is(expected.getErrorMessage()));
  }
}
