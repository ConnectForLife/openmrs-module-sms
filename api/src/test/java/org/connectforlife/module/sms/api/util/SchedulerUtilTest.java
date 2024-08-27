/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.connectforlife.module.sms.ContextMockedTest;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;

public class SchedulerUtilTest extends ContextMockedTest {

  private static final String TASK_NAME_PREFIX_1 = "testName";

  private static final String TASK_NAME_PREFIX_2 = "otherName";

  @Test
  public void shouldGetTasksByNamePrefix() {
    when(schedulerService.getRegisteredTasks()).thenReturn(buildTestTasks());

    List<TaskDefinition> actual = SchedulerUtil.getTasksByNamePrefix(TASK_NAME_PREFIX_1);

    assertEquals(2, actual.size());
    actual.forEach(task -> assertTrue(isTextStartWithPrefix(task.getName(), TASK_NAME_PREFIX_1)));
  }

  @Test
  public void shouldScheduleTask() throws SchedulerException {
    TaskDefinition task = buildTestTask(TASK_NAME_PREFIX_1);

    SchedulerUtil.scheduleTask(task);

    verify(schedulerService).saveTaskDefinition(task);
    verify(schedulerService).scheduleTask(task);
  }

  @Test
  public void shouldDeleteTask() throws SchedulerException {
    TaskDefinition task = buildTestTask(TASK_NAME_PREFIX_1);

    SchedulerUtil.deleteTask(task);

    verify(schedulerService).shutdownTask(task);
    verify(schedulerService).deleteTask(task.getId());
  }

  private List<TaskDefinition> buildTestTasks() {
    return Arrays.asList(
        buildTestTask(TASK_NAME_PREFIX_1 + "1"),
        buildTestTask(TASK_NAME_PREFIX_1 + "2"),
        buildTestTask(TASK_NAME_PREFIX_2 + "1"),
        buildTestTask(TASK_NAME_PREFIX_2 + "1")
    );
  }

  private TaskDefinition buildTestTask(String taskName) {
    TaskDefinition task = new TaskDefinition();
    task.setId(RandomUtils.nextInt());
    task.setName(taskName);
    task.setTaskClass("org.openmrs.module.sms.api.scheduler.job.ScheduledMessageJob");
    task.setStartTime(DateUtil.now());
    task.setStarted(true);
    task.setStartOnStartup(false);

    return task;
  }

  private boolean isTextStartWithPrefix(String text, String prefix) {
    return text.startsWith(prefix);
  }
}
