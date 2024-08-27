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

import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.util.TestUtil;
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

@WebAppConfiguration
public class WhatsappControllerBundleITTest extends BaseModuleWebContextSensitiveTest {

	private static final String CONFIG_NAME = "sample-it-config";

	private static final String MISSING_CONFIG_NAME = "missing-config";

	private static final String TEMPLATE_NAME = "Plivo";

	@Autowired
	@Qualifier("sms.configService")
	private ConfigService configService;

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
	public void cleanUpDatabase() throws Exception {
		this.deleteAllData();
	}

	@After
	public void restoreConfigs() {
		configService.updateConfigs(backupConfigs);
	}

	@Test
	public void handleWhatsappControllerFunctionalMissingConfig() throws Exception {
		mockMvc
				.perform(
						post(String.format("/whatsapp/%s", MISSING_CONFIG_NAME))
								.contentType(TestUtil.APPLICATION_JSON)
								.content(TestUtil.encodeString()))
				.andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	public void handleWhatsappControllerFunctionalExistsConfig() throws Exception {
		mockMvc
				.perform(
						post(String.format("/whatsapp/%s", CONFIG_NAME))
								.contentType(MediaType.parseMediaType(TestUtil.APPLICATION_JSON))
								.content(TestUtil.encodeString()))
				.andExpect(status().is(HttpStatus.OK.value()));
	}
}
