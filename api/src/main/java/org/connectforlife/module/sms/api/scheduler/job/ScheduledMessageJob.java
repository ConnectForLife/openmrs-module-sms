/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.scheduler.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSInputSourceProcessorContext;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSSQLDataSetProcessor;
import org.connectforlife.module.sms.api.adhocsms.ScheduledMessageDetails;
import org.connectforlife.module.sms.api.data.AdHocSMSData;
import org.connectforlife.module.sms.api.service.AdHocSMSInputSourceProcessorService;
import org.connectforlife.module.sms.api.service.ScheduleAdHocSMSesService;
import org.connectforlife.module.sms.api.util.CronUtil;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.ScheduledMessageDetailsUtil;
import org.connectforlife.module.sms.api.util.SchedulerUtil;
import org.connectforlife.module.sms.api.util.SmsTaskUtil;
import org.openmrs.scheduler.tasks.AbstractTask;

public class ScheduledMessageJob extends AbstractTask {

  public static final String JOB_NAME_PREFIX = "ScheduledMessage";

  public static final String JOB_PROPERTIES_KEY = "SCHEDULED_MESSAGE_PROPERTIES";

  private static final Log LOGGER = LogFactory.getLog(ScheduledMessageJob.class);

  private ScheduledMessageDetails scheduledMessageDetails;

  @Override
  public void execute() {
    try {
      scheduledMessageDetails = ScheduledMessageDetailsUtil.fromJSONString(
          taskDefinition.getProperty(JOB_PROPERTIES_KEY));
      String reportUuid = scheduledMessageDetails.getReportUuid();
      List<AdHocSMSData> smsData = Context.getService(AdHocSMSInputSourceProcessorService.class)
          .getAdHocSMSData(
              buildContextForSQLDataset(reportUuid));
      setSMSesDeliveryTime(smsData);
      if (CollectionUtils.isNotEmpty(smsData)) {
        Context.getService(ScheduleAdHocSMSesService.class).scheduleAdHocSMSes(smsData);
        taskDefinition.setLastExecutionTime(DateUtil.now());
      } else {
        LOGGER.info("No SMSes scheduled to be sent");
      }
    } finally {
      scheduleJobForNextExecution();
    }
  }

  public static String getTaskName(ScheduledMessageDetails messageDetails) {
    String taskName = JOB_NAME_PREFIX + ":" + messageDetails.getName();
    return taskName.substring(0, Math.min(taskName.length(), SmsTaskUtil.NAME_MAX_LENGTH));
  }

  public static Class getTaskClass() {
    return ScheduledMessageJob.class;
  }

  private AdHocSMSInputSourceProcessorContext buildContextForSQLDataset(String dataSetUuid) {
    Map<String, String> options = new HashMap<>();
    options.put(AdHocSMSSQLDataSetProcessor.DATA_SET_UUID_PROP_NAME, dataSetUuid);

    return new AdHocSMSInputSourceProcessorContext(null, options);
  }

  private void setSMSesDeliveryTime(List<AdHocSMSData> smsDataList) {
    String now = DateUtil.getDateInGivenFormat(DateUtil.now(), DateUtil.BASIC_DATE_TIME_FORMAT);
    smsDataList.forEach(sms -> sms.setContactTime(now));
  }

  private void scheduleJobForNextExecution() {
    taskDefinition.setStartTime(
        CronUtil.getNextDate(scheduledMessageDetails.getCronExpression(), DateUtil.now()));
    SchedulerUtil.scheduleTask(taskDefinition);
  }
}
