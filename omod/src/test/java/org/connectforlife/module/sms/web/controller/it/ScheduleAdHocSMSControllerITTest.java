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

package org.connectforlife.module.sms.web.controller.it;

import org.apache.commons.io.IOUtils;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
public class ScheduleAdHocSMSControllerITTest extends BaseModuleWebContextSensitiveTest {
  private static final String TEST_XLSX_RESOURCE_PATH =
      "/ScheduleAdHocSMSControllerITTest/test-sms.xlsx";
  private static final String TEST_XLSX_EMPTY_RESOURCE_PATH =
      "/ScheduleAdHocSMSControllerITTest/test-sms-empty.xlsx";
  private static final String XLSX_MIME_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private SchedulerService schedulerService;
  @Autowired private DataSetDefinitionService dataSetDefinitionService;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    super.initMocks();
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @After
  public void cleanUpDatabase() throws Exception {
    this.deleteAllData();
  }

  @Test
  public void shouldScheduleAdHocSMSesFromFile() throws Exception {
    final MockMultipartFile testFile = readXLSXTestFile(TEST_XLSX_RESOURCE_PATH);
    mockMvc
        .perform(fileUpload("/scheduleAdHocSMS").file(testFile))
        .andExpect(status().is(HttpStatus.OK.value()));

    Collection<TaskDefinition> taskDefinitions = schedulerService.getScheduledTasks();
    Assert.assertFalse(taskDefinitions.isEmpty());
    TaskDefinition firstTask = taskDefinitions.iterator().next();
    Assert.assertTrue(firstTask.getName().startsWith(SmsEventSubjectsConstants.SCHEDULED));
  }

  @Test
  public void shouldNotScheduleAdHocSMSesForEmptyFile() throws Exception {
    final MockMultipartFile testFile = readXLSXTestFile(TEST_XLSX_EMPTY_RESOURCE_PATH);
    mockMvc
        .perform(fileUpload("/scheduleAdHocSMS").file(testFile))
        .andExpect(status().is(HttpStatus.OK.value()));

    Collection<TaskDefinition> taskDefinitions = schedulerService.getScheduledTasks();
    Assert.assertTrue(taskDefinitions.isEmpty());
  }

  @Test
  public void shouldScheduleAdHocSMSesFromDataSet() throws Exception {
    final SqlDataSetDefinition testDataSet = createTestDataSetWithData();

    mockMvc
        .perform(post("/scheduleAdHocSMS/" + testDataSet.getUuid()))
        .andExpect(status().is(HttpStatus.OK.value()));

    Collection<TaskDefinition> taskDefinitions = schedulerService.getScheduledTasks();
    Assert.assertFalse(taskDefinitions.isEmpty());
    TaskDefinition firstTask = taskDefinitions.iterator().next();
    Assert.assertTrue(firstTask.getName().startsWith(SmsEventSubjectsConstants.SCHEDULED));
  }

  @Test
  public void shouldNotScheduleAdHocSMSesForEmptyDataSet() throws Exception {
    final SqlDataSetDefinition testDataSet = createTestDataSetWithoutData();

    mockMvc
        .perform(post("/scheduleAdHocSMS/" + testDataSet.getUuid()))
        .andExpect(status().is(HttpStatus.OK.value()));

    Collection<TaskDefinition> taskDefinitions = schedulerService.getScheduledTasks();
    Assert.assertTrue(taskDefinitions.isEmpty());
  }

  private MockMultipartFile readXLSXTestFile(String resourcePath) throws IOException {
    final ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
    final InputStream xlsxFile =
        ScheduleAdHocSMSControllerITTest.class.getResourceAsStream(resourcePath);

    IOUtils.copy(xlsxFile, resultStream);

    return new MockMultipartFile("file", "file.xlsx", XLSX_MIME_TYPE, resultStream.toByteArray());
  }

  private SqlDataSetDefinition createTestDataSetWithData() {
    final SqlDataSetDefinition sqlDataSetDefinition =
        new SqlDataSetDefinition(
            "ScheduleAdHocSMSControllerITTest",
            "",
            "SELECT '48600123321' as \"Phone\", 'Hello from SQL' as \"SMS Text\", '{}' as \"Parameters\", "
                + "'2022-12-05 16:00:00' as \"Contact time\", 'sms-config' as \"Config\"");

    return dataSetDefinitionService.saveDefinition(sqlDataSetDefinition);
  }

  private SqlDataSetDefinition createTestDataSetWithoutData() {
    final SqlDataSetDefinition sqlDataSetDefinition =
        new SqlDataSetDefinition(
            "ScheduleAdHocSMSControllerITTest",
            "",
            "SELECT '' as \"Phone\", '' as \"SMS Text\", '' as \"Parameters\", "
                + "'' as \"Contact time\", '' as \"Config\" FROM dual WHERE 1 != 1");

    return dataSetDefinitionService.saveDefinition(sqlDataSetDefinition);
  }
}
