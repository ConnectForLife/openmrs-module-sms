/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.audit;

import org.connectforlife.module.sms.builder.SmsRecordsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateUtil.class})
public class SmsLoggingRecordsTest {

  private static final int EXPECTED_PAGE = 1;
  private static final int EXPECTED_ROWS = 52;
  private static final long EXPECTED_TOTAL_RECORDS = 225;
  private static final String EXPECTED_DATE = "2010-11-16 15:40";

  @Before
  public void setUp() throws Exception {
    mockStatic(DateUtil.class);
    doReturn(EXPECTED_DATE).when(DateUtil.class, "getDateWithLocalTimeZone", anyObject());
  }

  @Test
  public void shouldMapSmsRecordsAsExpected() {
    SmsRecords expected = new SmsRecordsBuilder().build();
    SmsLoggingRecords actual =
        new SmsLoggingRecords(EXPECTED_PAGE, EXPECTED_ROWS, EXPECTED_TOTAL_RECORDS, expected);
    assertThat(actual.getRows().size(), is(expected.getRecords().size()));
    assertThat(actual.getPage(), is(EXPECTED_PAGE));
    assertThat(actual.getRecords(), is(EXPECTED_TOTAL_RECORDS));
    assertSmsRecords(actual.getRows().get(0), expected.getRecords().get(0));
  }

  @Test
  public void verifyToString() {
    SmsRecords smsRecords = new SmsRecordsBuilder().build();
    SmsLoggingRecords smsLoggingRecords =
        new SmsLoggingRecords(EXPECTED_PAGE, EXPECTED_ROWS, EXPECTED_TOTAL_RECORDS, smsRecords);
    String expected = smsLoggingRecords.toString();
    String actual =
        String.format(
            "SmsLoggingRecords{page=%d, total=%d, records=%d, rows=%s}",
            smsLoggingRecords.getPage(),
            smsLoggingRecords.getTotal(),
            smsLoggingRecords.getRecords(),
            smsLoggingRecords.getRows());
    assertThat(actual, is(expected));
  }

  private void assertSmsRecords(SmsLoggingRecord actual, SmsRecord expected) {
    assertThat(actual.getConfig(), is(expected.getConfig()));
    assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
    assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
    assertThat(actual.getTimestamp(), is(EXPECTED_DATE));
    assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
    assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
    assertThat(actual.getOpenMrsId(), is(expected.getOpenMrsId()));
    assertThat(actual.getProviderId(), is(expected.getProviderId()));
  }
}
