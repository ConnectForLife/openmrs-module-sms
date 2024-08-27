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

import java.text.ParseException;
import java.util.Date;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.quartz.CronExpression;

public final class CronUtil {

  public static Date getNextDate(String cronExpression, Date startDate) {
    try {
      CronExpression cron = new CronExpression(cronExpression);
      return cron.getNextValidTimeAfter(startDate);
    } catch (ParseException ex) {
      throw new SmsRuntimeException(
          String.format("Unable to parse cron expression %s", cronExpression), ex);
    }
  }

  private CronUtil() {
  }
}
