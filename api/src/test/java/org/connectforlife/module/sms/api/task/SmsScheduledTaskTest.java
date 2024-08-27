/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.connectforlife.module.sms.api.http.SmsHttpService;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.SmsEventParamsConstants;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class SmsScheduledTaskTest {

  private static final String OTHER_PROP = "other";
  private static final String FAILURE_COUNT = "2";
  private static final String DELIVERY_TIME = DateUtil.dateToString(DateUtil.now());
  private static final String RECIPIENTS = "5353135321,2151553333";
  private static final String CUSTOM_PARAMS = "key1=value1,key2=value2";
  private SmsHttpService smsHttpService;
  private SchedulerService schedulerService;

  @Before
  public void setUp() throws Exception {
    mockStatic(Context.class);
    smsHttpService = mock(SmsHttpService.class);
    doReturn(smsHttpService)
        .when(Context.class, "getRegisteredComponent", "sms.SmsHttpService", SmsHttpService.class);
    schedulerService = mock(SchedulerService.class);
    doReturn(schedulerService).when(Context.class, "getSchedulerService");
  }

  @Test
  public void execute() {
    SmsScheduledTask task = prepareTask();
    task.execute();
    verify(smsHttpService).send(anyObject());
  }

  @Test(expected = SmsRuntimeException.class)
  public void shutdownTaskShouldThrowRuntimeException() throws Exception {
    doThrow(new SchedulerException()).when(schedulerService).shutdownTask(anyObject());
    SmsScheduledTask task = prepareTask();
    task.execute();
  }

  private SmsScheduledTask prepareTask() {
    SmsScheduledTask task = new SmsScheduledTask();
    Map<String, String> properties = new HashMap<>();
    properties.put(SmsEventParamsConstants.CUSTOM_PARAMS, CUSTOM_PARAMS);
    properties.put(SmsEventParamsConstants.RECIPIENTS, RECIPIENTS);
    properties.put(SmsEventParamsConstants.DELIVERY_TIME, DELIVERY_TIME);
    properties.put(SmsEventParamsConstants.FAILURE_COUNT, FAILURE_COUNT);
    properties.put(OTHER_PROP, OTHER_PROP);
    TaskDefinition taskDefinition = new TaskDefinition();
    taskDefinition.setProperties(properties);
    ReflectionTestUtils.setField(task, "taskDefinition", taskDefinition);
    return task;
  }
}
