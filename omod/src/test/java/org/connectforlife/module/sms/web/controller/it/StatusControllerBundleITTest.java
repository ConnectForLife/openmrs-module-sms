/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.web.controller.it;

import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Template;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.util.TestUtil;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verify StatusController present & functional. */
@WebAppConfiguration
public class StatusControllerBundleITTest extends BaseModuleWebContextSensitiveTest {

  private static final String CONFIG_NAME = "sample-it-config";

  private static final String INVALID_CONFIG_NAME = "invalid-sample-it-config";

  public static final String TEMPLATE_NAME = "Plivo";
  public static final int EXPECTED_OLD_PLUS_NEW = 2;
  public static final int NEW_RECORD_ID = 1;

  private MockMvc mockMvc;

  private Configs backupConfigs;

  @Autowired
  @Qualifier("sms.configService")
  private ConfigService configService;

  @Autowired
  @Qualifier("sms.SmsRecordDao")
  private SmsRecordDao smsRecordDao;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired
  @Qualifier("templateService")
  private TemplateService templateService;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @After
  public void cleanUpDatabase() throws Exception {
    this.deleteAllData();
  }

  @Before
  public void createConfigs() {
    templateService.loadTemplates();
    backupConfigs = configService.getConfigs();
    Config config = new Config();
    config.setName(CONFIG_NAME);
    config.setTemplateName(TEMPLATE_NAME);

    Configs configs = new Configs();
    configs.setConfigList(singletonList(config));
    configs.setDefaultConfigName(CONFIG_NAME);

    configService.updateConfigs(configs);
  }

  @After
  public void restoreConfigs() {
    configService.updateConfigs(backupConfigs);
  }

  @Test
  public void handleWithExistingConfig() throws Exception {
    // Create & send a CDR status callback
    String messageId = UUID.randomUUID().toString();
    createSMSRecord(messageId);
    mockMvc
        .perform(
            post(String.format("/sms/status/%s", CONFIG_NAME))
                .param("Status", "read")
                .param("From", "+12065551212")
                .param("To", "+12065551313")
                .param("MessageUUID", UUID.randomUUID().toString())
                .contentType(TestUtil.APPLICATION_JSON)
                .content(TestUtil.encodeString()))
        .andExpect(status().is(HttpStatus.OK.value()));

    // Verify we logged this
    List<SmsRecord> smsRecords = smsRecordDao.getAll(true);
    assertEquals(1, smsRecords.size());
    assertEquals(smsRecords.get(0).getProviderStatus(), null);
  }

  @Test
  public void handleWithExistingConfigAndExistingRecord() throws Exception {
    String messageId = UUID.randomUUID().toString();
    createSMSRecord(messageId);
    mockMvc
        .perform(
            post(String.format("/sms/status/%s", CONFIG_NAME))
                .param("Status", "delivered")
                .param("From", "+12065551212")
                .param("To", "+12065551313")
                .param("MessageUUID", messageId)
                .contentType(TestUtil.APPLICATION_JSON)
                .content(TestUtil.encodeString()))
        .andExpect(status().is(HttpStatus.OK.value()));

    // Verify we logged this
    List<SmsRecord> smsRecords = smsRecordDao.getAll(true);
    assertEquals(2, smsRecords.size());
    assertEquals(smsRecords.get(1).getProviderStatus(), "delivered");
  }

  private void createSMSRecord(String messageId) {
    SmsRecord smsRecord = new SmsRecord();
    smsRecord.setConfig(CONFIG_NAME);
    smsRecord.setProviderId(messageId);
    smsRecordDao.saveOrUpdate(smsRecord);
  }

  @Test
  public void handleNotProcessWhenConfigWithoutMessageId() throws Exception {
    Template template = templateService.getTemplate(TEMPLATE_NAME);
    template.getStatus().setMessageIdKey(null);
    String messageId = UUID.randomUUID().toString();
    mockMvc
        .perform(
            post(String.format("/sms/status/%s", CONFIG_NAME))
                .param("Status", "sent")
                .param("From", "+12065551212")
                .param("To", "+12065551313")
                .param("MessageUUID", messageId)
                .contentType(TestUtil.APPLICATION_JSON)
                .content(TestUtil.encodeString()))
        .andExpect(status().is(HttpStatus.OK.value()));

    // Verify we logged this
    List<SmsRecord> smsRecords = smsRecordDao.getAll(true);
    assertEquals(0, smsRecords.size());
  }

  @Test
  public void handleNotProcessWhenConfigWithoutStatusKey() throws Exception {
    Template template = templateService.getTemplate(TEMPLATE_NAME);
    template.getStatus().setStatusKey(null);
    String messageId = UUID.randomUUID().toString();
    mockMvc
        .perform(
            post(String.format("/sms/status/%s", CONFIG_NAME))
                .param("Status", "sent")
                .param("From", "+12065551212")
                .param("To", "+12065551313")
                .param("MessageUUID", messageId)
                .contentType(TestUtil.APPLICATION_JSON)
                .content(TestUtil.encodeString()))
        .andExpect(status().is(HttpStatus.OK.value()));

    // Verify we logged this
    List<SmsRecord> smsRecords = smsRecordDao.getAll(true);
    assertEquals(0, smsRecords.size());
  }

  @Test(expected = NestedServletException.class)
  public void handleShouldThrowWhenInvalidConfig() throws Exception {
    // Create & send a CDR status callback
    String messageId = UUID.randomUUID().toString();
    mockMvc
        .perform(
            post(String.format("/sms/status/%s", INVALID_CONFIG_NAME))
                .param("Status", "sent")
                .param("From", "+12065551212")
                .param("To", "+12065551313")
                .param("MessageUUID", messageId)
                .contentType(TestUtil.APPLICATION_JSON)
                .content(TestUtil.encodeString()))
        .andExpect(status().isInternalServerError());
  }
}
