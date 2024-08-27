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

import java.util.StringJoiner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {

  private static final Log LOGGER = LogFactory.getLog(DateUtil.class);

  private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

  private static final String DEFAULT_TIME_ZONE = "UTC";

  public static final String HOUR_AND_MINUTE_PATTERN = "HH:mm";

  public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

  public static final String BASIC_DATE_TIME_FORMAT = new StringJoiner(" ").add(SIMPLE_DATE_FORMAT)
      .add(HOUR_AND_MINUTE_PATTERN).toString();

  public static Date parse(String dateTime) {
    return parse(dateTime, null);
  }

  public static Date parse(String dateTime, String pattern) {
    String datePattern = pattern;
    if (StringUtils.isBlank(pattern)) {
      datePattern = ISO_DATE_TIME_FORMAT;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
    Date result = null;
    try {
      result = simpleDateFormat.parse(dateTime);
    } catch (ParseException e) {
      LOGGER.error(
          String.format("Could not parse `%s` date using `%s` pattern", dateTime, datePattern));
    }
    return result;
  }

  public static Date now() {
    return getDateWithDefaultTimeZone(new Date());
  }

  public static Date plusDays(Date date, int duration) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, duration);
    return calendar.getTime();
  }

  public static Date getDateWithDefaultTimeZone(Date timestamp) {
    if (timestamp == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
    calendar.setTime(timestamp);
    return calendar.getTime();
  }

  public static String getDateWithLocalTimeZone(Date timestamp) {
    if (timestamp == null) {
      return StringUtils.EMPTY;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
    simpleDateFormat.setTimeZone(getLocalTimeZone());
    return simpleDateFormat.format(timestamp);
  }

  public static String dateToString(Date date, TimeZone timeZone) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
    if (timeZone != null) {
      simpleDateFormat.setTimeZone(timeZone);
    }
    return simpleDateFormat.format(date);
  }

  public static String dateToString(Date date) {
    return dateToString(date, TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
  }

  public static TimeZone getLocalTimeZone() {
    String userTimeZoneName =
        Context.getAdministrationService().getGlobalProperty(SMSConstants.DEFAULT_USER_TIMEZONE);
    if (StringUtils.isNotBlank(userTimeZoneName)) {
      return TimeZone.getTimeZone(ZoneId.of(userTimeZoneName));
    }
    return TimeZone.getTimeZone(DEFAULT_TIME_ZONE);
  }

  public static Date getDateWithTimeOfDay(Date date, String timeOfDay, TimeZone timeZone) {
    final SimpleDateFormat timeFormat = new SimpleDateFormat(HOUR_AND_MINUTE_PATTERN);
    timeFormat.setTimeZone(timeZone);

    try {
      final Calendar timePart = DateUtils.toCalendar(timeFormat.parse(timeOfDay));
      timePart.setTimeZone(timeZone);

      final Calendar result = org.apache.commons.lang3.time.DateUtils.toCalendar(date);
      result.setTimeZone(timeZone);
      result.set(Calendar.HOUR_OF_DAY, timePart.get(Calendar.HOUR_OF_DAY));
      result.set(Calendar.MINUTE, timePart.get(Calendar.MINUTE));
      result.set(Calendar.SECOND, 0);
      result.set(Calendar.MILLISECOND, 0);
      return result.getTime();
    } catch (ParseException pe) {
      throw new IllegalArgumentException(
          "Illegal value of timeOfDay="
              + timeOfDay
              + "; expected pattern: "
              + HOUR_AND_MINUTE_PATTERN,
          pe);
    }
  }

  public static String getDateInGivenFormat(Date date, String dateFormat) {
    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
    return formatter.format(date);
  }

  private DateUtil() {
  }
}
