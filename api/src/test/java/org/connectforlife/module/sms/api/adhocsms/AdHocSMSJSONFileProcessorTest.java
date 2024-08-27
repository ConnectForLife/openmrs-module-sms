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
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.connectforlife.module.sms.api.data.AdHocSMSData;

public class AdHocSMSJSONFileProcessorTest extends AdHocSMSInputSourceProcessorTestHelper {

  private static final String TEST_JSON_FILE_NAME = "testAdHocSMSJSONFile.json";

  @Test
  public void shouldGetAdHocSMSDataFromJSONFile() {
    List<AdHocSMSData> actual = new AdHocSMSJSONFileProcessor().getAdHocSMSData(
        buildInputSourceProcessorContext());

    assertNotNull(actual);
    assertEquals(3, actual.size());
    verifyResults(actual.get(0), "123456789", "test json msg1", "18:20", "nexmoWhatsapp1");
    verifyResults(actual.get(1), "987654321", "test json msg2", "18:21", "nexmoWhatsapp2");
    verifyResults(actual.get(2), "1122334455", "test json msg3", "18:22", "nexmoWhatsapp3");
  }

  private AdHocSMSInputSourceProcessorContext buildInputSourceProcessorContext() {
    InputStream inputStream = getInputStream(AD_HOC_SMS_RESOURCE_BASE_PATH + TEST_JSON_FILE_NAME);
    Map<String, String> contextOptionsMap = new HashMap<>();
    contextOptionsMap.put("fileExtension", "json");
    contextOptionsMap.put("sheetName", null);
    return new AdHocSMSInputSourceProcessorContext(inputStream, contextOptionsMap);
  }
}
