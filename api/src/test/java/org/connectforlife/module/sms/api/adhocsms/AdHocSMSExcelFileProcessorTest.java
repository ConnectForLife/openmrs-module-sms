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

public class AdHocSMSExcelFileProcessorTest extends AdHocSMSInputSourceProcessorTestHelper {

  private static final String TEST_EXCEL_FILE_NAME = "testAdHocSMSExcelFile.xlsx";

  @Test
  public void shouldGetAdHocSMSDataFromExcelFile() {
    List<AdHocSMSData> actual = new AdHocSMSExcelFileProcessor().getAdHocSMSData(
        buildInputSourceProcessorContext());

    assertNotNull(actual);
    assertEquals(3, actual.size());
    verifyResults(actual.get(0), "111222333", "test text msg first", "10:30", "textConfig1");
    verifyResults(actual.get(1), "444555666", "test text msg second", "12:30", "textConfig2");
    verifyResults(actual.get(2), "777888999", "test text third", "14:30", "textConfig3");
  }

  private AdHocSMSInputSourceProcessorContext buildInputSourceProcessorContext() {
    InputStream inputStream = getInputStream(AD_HOC_SMS_RESOURCE_BASE_PATH + TEST_EXCEL_FILE_NAME);
    Map<String, String> contextOptionsMap = new HashMap<>();
    contextOptionsMap.put("fileExtension", "xlsx");
    contextOptionsMap.put("sheetName", null);
    return new AdHocSMSInputSourceProcessorContext(inputStream, contextOptionsMap);
  }
}
