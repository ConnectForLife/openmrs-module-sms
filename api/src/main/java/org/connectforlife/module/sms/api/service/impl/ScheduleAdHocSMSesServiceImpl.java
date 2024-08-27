/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.service.SmsSchedulerService;
import org.openmrs.api.context.Context;
import org.connectforlife.module.sms.api.data.AdHocSMSData;
import org.connectforlife.module.sms.api.event.SmsEvent;
import org.connectforlife.module.sms.api.service.ScheduleAdHocSMSesService;
import org.connectforlife.module.sms.api.task.SmsScheduledTask;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.OpenMRSIDGeneratorUtil;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;
import org.connectforlife.module.sms.api.util.SmsEventsHelper;

public class ScheduleAdHocSMSesServiceImpl implements ScheduleAdHocSMSesService {

  private static final Log LOGGER = LogFactory.getLog(ScheduleAdHocSMSesServiceImpl.class);

  @Override
  public void scheduleAdHocSMSes(List<AdHocSMSData> smsData) {
    SmsSchedulerService smsSchedulerService = Context.getService(SmsSchedulerService.class);
    smsData.forEach(sms -> scheduleSMS(sms, smsSchedulerService));
  }

  private void scheduleSMS(AdHocSMSData smsData, SmsSchedulerService smsSchedulerService) {
    SmsEvent smsEvent = buildSMSEvent(smsData);
    Date deliveryDate = getSMSDeliveryDate(smsData.getContactTime());
    if (deliveryDate != null) {
      smsSchedulerService.safeScheduleRunOnceJob(smsEvent, deliveryDate, new SmsScheduledTask());
    } else {
      LOGGER.warn(String.format(
          "Scheduling SMS will be skipped because following date: %s has invalid format",
          smsData.getContactTime()));
    }
  }

  private SmsEvent buildSMSEvent(AdHocSMSData smsData) {
    return SmsEventsHelper.outboundEvent(
        SmsEventSubjectsConstants.SCHEDULED,
        smsData.getConfig(),
        Collections.singletonList(smsData.getPhone()),
        smsData.getSmsText().replace('\n', ' '),
        OpenMRSIDGeneratorUtil.generateOpenMRSID(),
        null,
        null,
        null,
        null,
        smsData.getParameters()
    );
  }

  private Date getSMSDeliveryDate(String dateTime) {
    return DateUtil.parse(dateTime, DateUtil.BASIC_DATE_TIME_FORMAT);
  }
}
