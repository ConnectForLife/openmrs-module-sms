/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.adhocsms;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import org.connectforlife.module.sms.api.data.AdHocSMSData;

public class AdHocSMSInputSourceProcessorTestHelper {

  protected static final String AD_HOC_SMS_RESOURCE_BASE_PATH = "adhocsms/";

  protected void verifyResults(AdHocSMSData adHocSMSData, String phoneNumber, String smsText,
      String contactTime, String config) {
    assertEquals(phoneNumber, adHocSMSData.getPhone());
    assertEquals(smsText, adHocSMSData.getSmsText());
    assertEquals(contactTime, adHocSMSData.getContactTime());
    assertEquals(config, adHocSMSData.getConfig());
  }

  protected InputStream getInputStream(String fileName) {
    return this.getClass().getClassLoader().getResourceAsStream(fileName);
  }
}
