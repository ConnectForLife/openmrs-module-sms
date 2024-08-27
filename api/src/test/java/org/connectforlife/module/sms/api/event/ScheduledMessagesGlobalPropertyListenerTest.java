/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.event;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.GlobalProperty;
import org.connectforlife.module.sms.ContextMockedTest;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;

public class ScheduledMessagesGlobalPropertyListenerTest extends ContextMockedTest {

  private static final String TEST_JSON_STRING = "[{\"name\":\"testName\",\"description\":\"testDescription\",\"reportUuid\":\"a4236158-457e-11ed-b38c-0242ac140002\",\"cronExpression\":\"0 */15 * ? * *\"}]";

  private static final String TEST_GP_UUID = "30da4f63-4585-11ed-b38c-0242ac140002";

  @Mock
  private MapMessage message;

  @Test
  public void shouldPerformActionOnUpdatingGP() throws JMSException, SchedulerException {
    when(administrationService.getGlobalPropertyByUuid(TEST_GP_UUID)).thenReturn(buildTestGlobalProperty());
    when(message.getString("uuid")).thenReturn(TEST_GP_UUID);

    new ScheduledMessagesGlobalPropertyListener().performAction(message);

    verify(administrationService).getGlobalPropertyByUuid(TEST_GP_UUID);
    verify(schedulerService).getRegisteredTasks();
    verify(schedulerService).saveTaskDefinition(any(TaskDefinition.class));
    verify(schedulerService).scheduleTask(any(TaskDefinition.class));
  }

  private GlobalProperty buildTestGlobalProperty() {
    GlobalProperty globalProperty = new GlobalProperty();
    globalProperty.setProperty("sms.scheduledMessagesDetails");
    globalProperty.setPropertyValue(TEST_JSON_STRING);

    return globalProperty;
  }

}
