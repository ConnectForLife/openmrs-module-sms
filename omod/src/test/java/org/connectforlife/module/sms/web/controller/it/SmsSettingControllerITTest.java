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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
public class SmsSettingControllerITTest extends BaseModuleWebContextSensitiveTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getTemplates() throws Exception {

		MvcResult result = mockMvc.perform(get("/sms/templates"))
				.andExpect(status().is(HttpStatus.OK.value())).andReturn();
		assertThat(result, is(notNullValue()));
	}

	@Test
	public void getConfigs() throws Exception {

		MvcResult result = mockMvc.perform(get("/sms/configs"))
				.andExpect(status().is(HttpStatus.OK.value())).andReturn();
		assertThat(result, is(notNullValue()));
	}

	@Test
	public void getCustomUISettings() throws Exception {

		MvcResult result = mockMvc.perform(get("/sms/mds-databrowser-config"))
				.andExpect(status().is(HttpStatus.OK.value())).andReturn();
		assertThat(result, is(notNullValue()));
	}

	@Test
	public void handleException() throws Exception {

		mockMvc.perform(post("/sms/configs"))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

	@After
	public void cleanUpDatabase() throws Exception {

		this.deleteAllData();
	}
}
