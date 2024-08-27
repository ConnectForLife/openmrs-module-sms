/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.connectforlife.module.sms.api.event.SmsEvent;
import org.connectforlife.module.sms.api.task.AbstractSmsTask;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class SmsSchedulerServiceImpl extends BaseOpenmrsService implements SmsSchedulerService {

    private static final Log LOGGER = LogFactory.getLog(SmsSchedulerServiceImpl.class);
    private static final long RUN_ONCE = 0;

    private SchedulerService schedulerService;

    @Override
    public void safeScheduleRunOnceJob(SmsEvent event, Date startTime, AbstractSmsTask task) {
        String taskName = event.generateTaskName();
        shutdownTask(taskName);

        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setName(taskName);
        taskDefinition.setTaskClass(task.getClass().getName());
        taskDefinition.setStartTime(startTime);
        taskDefinition.setStartOnStartup(Boolean.FALSE);
        taskDefinition.setProperties(event.convertProperties());
        taskDefinition.setRepeatInterval(RUN_ONCE);

        try {
            schedulerService.saveTaskDefinition(taskDefinition);
            schedulerService.scheduleTask(taskDefinition);
        } catch (SchedulerException ex) {
            throw new SmsRuntimeException(ex);
        }
    }

    private void shutdownTask(String taskName) {
        try {
            schedulerService.shutdownTask(schedulerService.getTaskByName(taskName));
        } catch (SchedulerException ex) {
            LOGGER.error(ex);
        }
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
