/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.configs;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;

public class ConfigTest {

  private static final int FAILURE_COUNT = 3;
  private Config config;

  @Before
  public void setup() {
    config = new Config();
    config.setMaxRetries(FAILURE_COUNT);
  }

  @Test
  public void shouldReturnRetryThenAbortSubject() {
    assertEquals(SmsEventSubjectsConstants.RETRYING, config.retryOrAbortSubject(FAILURE_COUNT - 1));
    assertEquals(SmsEventSubjectsConstants.ABORTED, config.retryOrAbortSubject(FAILURE_COUNT));
  }

  @Test
  public void shouldReturnRetryThenAbortStatus() {
    assertEquals(DeliveryStatusesConstants.RETRYING, config.retryOrAbortStatus(FAILURE_COUNT - 1));
    assertEquals(DeliveryStatusesConstants.ABORTED, config.retryOrAbortStatus(FAILURE_COUNT));
  }
}
