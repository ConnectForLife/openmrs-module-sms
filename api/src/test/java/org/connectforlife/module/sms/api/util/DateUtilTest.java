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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

//CHECKSTYLE:OFF: MagicNumber
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateUtil.class, Context.class, TimeZone.class})
public class DateUtilTest {

    private static final String ASIA_ALMATY = "Asia/Almaty";

    @Test
    public void shouldSuccessfullyParseDateTime() {
        Date expected = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, ASIA_ALMATY);
        Date actual = DateUtil.parse("2010-11-16T15:43:59.000+06:00");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldSuccessfullyAddDaysToDate() {
        Date expected = createDate(2010, Calendar.MARCH, 2, 15, 43, 59, "CET");
        Date date = createDate(2010, Calendar.FEBRUARY, 27, 15, 43, 59, "CET");
        Date actual = DateUtil.plusDays(date, 3);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldSuccessfullyConvertDateToUtcTimeZone() {
        Date expectedUtc = createDate(2010, Calendar.NOVEMBER, 16, 14, 43, 59, "UTC");
        Date dateCet = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "CET");
        Date actual = DateUtil.getDateWithDefaultTimeZone(dateCet);
        assertThat(actual, equalTo(expectedUtc));
    }

    @Test
    public void shouldReturnDateWithLocalTimeZone() throws Exception {
        Date date = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, ASIA_ALMATY);
        TimeZone timeZone = TimeZone.getTimeZone(ASIA_ALMATY);
        mockStatic(Context.class);
        AdministrationService administrationService = mock(AdministrationService.class);
        doReturn("").when(administrationService).getGlobalProperty(anyObject());
        doReturn(administrationService).when(Context.class, "getAdministrationService");
        mockStatic(TimeZone.class);
        BDDMockito.given(TimeZone.getDefault()).willReturn(timeZone);
        BDDMockito.given(TimeZone.getTimeZone((String) anyObject())).willReturn(timeZone);

        String expected = "2010-11-16T15:43:59.000+06:00";
        String actual = DateUtil.getDateWithLocalTimeZone(date);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldReturnConversionResultAsExpected() {
        String expectedDateAsString = "2012-01-10T00:00:00.000+06:00";
        Date actual = DateUtil.parse(expectedDateAsString);
        assertThat(DateUtil.dateToString(actual, TimeZone.getTimeZone(ASIA_ALMATY)), equalTo(expectedDateAsString));
    }

    @Test
    public void shouldReturnStringConvertingDate() {
        String expected = "2010-11-16T15:43:59.000Z";
        Date date = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "UTC");
        String actual = DateUtil.dateToString(date);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldReturnCorrectDateWithTimeOfDay() {
        final TimeZone testTimeZone = TimeZone.getTimeZone("UTC");
        final Calendar calendar = Calendar.getInstance(testTimeZone);
        calendar.set(2021, Calendar.MAY, 6, 13, 23, 45);
        calendar.set(Calendar.MILLISECOND, 765);
        final Date date = calendar.getTime();
        final String timeStr = "14:11";

        final Date result = DateUtil.getDateWithTimeOfDay(date, timeStr, testTimeZone);

        final Calendar expectedCalendar = Calendar.getInstance(testTimeZone);
        expectedCalendar.set(2021, Calendar.MAY, 6, 14, 11, 0);
        expectedCalendar.set(Calendar.MILLISECOND, 0);
        final Date expectedDate = expectedCalendar.getTime();

        assertNotNull(result);
        assertThat(result, is(expectedDate));
    }

    private Date createDate(int year, int month, int day, int hour, int minute, int second, String timezone) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        return calendar.getTime();
    }
}
//CHECKSTYLE:ON: MagicNumber
