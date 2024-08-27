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

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

public final class SchedulerUtil {

  public static List<TaskDefinition> getTasksByNamePrefix(String prefix) {
    return Context.getSchedulerService().getRegisteredTasks()
        .stream()
        .filter(task -> StringUtils.startsWith(task.getName(), prefix))
        .collect(Collectors.toList());
  }

  public static void scheduleTask(TaskDefinition task) {
    SchedulerService schedulerService = Context.getSchedulerService();
    try {
      schedulerService.saveTaskDefinition(task);
      schedulerService.scheduleTask(task);
    } catch (SchedulerException ex) {
      throw new SmsRuntimeException("Error while scheduling task", ex);
    }
  }

  public static void deleteTask(TaskDefinition task) {
    SchedulerService schedulerService = Context.getSchedulerService();
    try {
      schedulerService.shutdownTask(task);
      schedulerService.deleteTask(task.getId());
    } catch (SchedulerException ex) {
      throw new SmsRuntimeException("Error while deleting task", ex);
    }
  }

  private SchedulerUtil() {
  }
}
