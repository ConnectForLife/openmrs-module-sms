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

import org.codehaus.jackson.map.ObjectMapper;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.service.SmsService;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verify SendController present & functional.
 */
@WebAppConfiguration
public class SendControllerBundleITTest extends BaseModuleWebContextSensitiveTest {

    private static final String CONFIG_NAME = "sample-it-config";

    private static final String TEMPLATE_NAME = "Plivo";

    @Autowired
    @Qualifier("sms.configService")
    private ConfigService configService;

    @Autowired
    @Qualifier("sms.SmsService")
    private SmsService smsService;

    @Autowired
    @Qualifier("templateService")
    private TemplateService templateService;

   @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private Configs backupConfigs;

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
        config.setSplitFooter("Msg $m of $t");
        config.setSplitHeader("...");
        config.setExcludeLastFooter(true);
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
    public void verifyFunctional() throws Exception {
        OutgoingSms outgoingSms = new OutgoingSms(CONFIG_NAME, singletonList("12065551212"), "");
        ObjectMapper mapper = new ObjectMapper();
        String outgoingSmsJson = mapper.writeValueAsString(outgoingSms);

        mockMvc.perform(post("/sms/send")
                .contentType(MediaType.parseMediaType("application/json"))
                .content(outgoingSmsJson.getBytes("UTF-8")))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void verifyExceptionHandler() throws Exception {

      mockMvc.perform(post("/sms/send")
             .contentType(MediaType.parseMediaType("application/json")))
             .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

     }

  //TODO: responds the way an SMS provider would.

}
