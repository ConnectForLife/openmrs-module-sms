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

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.connectforlife.module.sms.ContextMockedTest;

public class GlobalPropertyUtilTest extends ContextMockedTest {

  private static final String TEST_KEY = "testKey";

  private static final String TEST_DEFAULT_VALUE = "testDefaultValue";

  @Test
  public void shouldReturnGPValue() {
    GlobalPropertyUtil.getGlobalProperty(TEST_KEY, TEST_DEFAULT_VALUE);

    verify(administrationService).getGlobalProperty(TEST_KEY, TEST_DEFAULT_VALUE);
  }
}
