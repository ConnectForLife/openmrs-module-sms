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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.Message;
import org.apache.commons.lang.StringUtils;
import org.connectforlife.module.sms.api.adhocsms.ScheduledMessageDetails;
import org.connectforlife.module.sms.api.scheduler.job.ScheduledMessageJob;
import org.openmrs.GlobalProperty;
import org.openmrs.event.Event.Action;
import org.connectforlife.module.sms.api.util.CronUtil;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.SMSConstants;
import org.connectforlife.module.sms.api.util.ScheduledMessageDetailsUtil;
import org.connectforlife.module.sms.api.util.SchedulerUtil;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduledMessagesGlobalPropertyListener extends GlobalPropertyActionListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      ScheduledMessagesGlobalPropertyListener.class);

  @Override
  protected List<String> subscribeToActions() {
    return Collections.singletonList(Action.UPDATED.name());
  }

  @Override
  public void performAction(Message message) {
    LOGGER.debug("Scheduled messages global property action listener triggered");
    GlobalProperty updatedGP = extractGlobalProperty(message);
    if (StringUtils.equals(updatedGP.getProperty(),
        SMSConstants.SCHEDULED_SMS_MESSAGES_DETAILS_GP_KEY)) {
      removeScheduledMessageJobs();
      scheduleMessages(updatedGP.getPropertyValue());
    }
  }

  private void removeScheduledMessageJobs() {
    List<TaskDefinition> tasksToRemove = SchedulerUtil.getTasksByNamePrefix(
        ScheduledMessageJob.JOB_NAME_PREFIX);
    tasksToRemove.forEach(SchedulerUtil::deleteTask);
  }

  private void scheduleMessages(String gpValue) {
    List<ScheduledMessageDetails> scheduledMessageDetails = ScheduledMessageDetailsUtil.convertJSONStringToObjectsList(
        gpValue);
    scheduledMessageDetails.forEach(this::createScheduledMessageJob);
  }

  private void createScheduledMessageJob(ScheduledMessageDetails messageDetails) {
    TaskDefinition task = prepareTask(messageDetails);
    SchedulerUtil.scheduleTask(task);
  }

  private TaskDefinition prepareTask(ScheduledMessageDetails messageDetails) {
    TaskDefinition task = new TaskDefinition();
    task.setName(ScheduledMessageJob.getTaskName(messageDetails));
    task.setRepeatInterval(0L);
    task.setTaskClass(ScheduledMessageJob.getTaskClass().getName());
    task.setStartTime(CronUtil.getNextDate(messageDetails.getCronExpression(), DateUtil.now()));
    task.setStartOnStartup(Boolean.FALSE);
    task.setProperties(wrapByMap(ScheduledMessageJob.JOB_PROPERTIES_KEY,
        ScheduledMessageDetailsUtil.toJSONString(messageDetails)));

    return task;
  }

  private Map<String, String> wrapByMap(String key, String value) {
    Map<String, String> map = new HashMap<>();
    map.put(key, value);
    return map;
  }
}
