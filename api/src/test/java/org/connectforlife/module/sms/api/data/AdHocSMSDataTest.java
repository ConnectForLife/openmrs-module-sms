package org.connectforlife.module.sms.api.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class AdHocSMSDataTest {

  @Test
  public void test() throws IOException {
    Map<String, String> resultMap = buildTestAdHocResultMap();

    AdHocSMSData actual = new AdHocSMSData(resultMap);

    assertNotNull(actual);
    assertEquals("111222333", actual.getPhone());
    assertEquals("test sms msg", actual.getSmsText());
    assertTrue(actual.getParameters().isEmpty());
    assertEquals("15:30", actual.getContactTime());
    assertEquals("testConfig", actual.getConfig());
  }

  @Test
  public void test2() {
    AdHocSMSData actual = buildTestAdHocSMSDataObject();

    assertNotNull(actual);
    assertEquals("555777999", actual.getPhone());
    assertEquals("test message", actual.getSmsText());
    assertTrue(actual.getParameters().isEmpty());
    assertEquals("2022-08-25 13:00", actual.getContactTime());
    assertEquals("test config", actual.getConfig());
  }

  private Map<String, String> buildTestAdHocResultMap() {
    Map<String, String> testMap = new HashMap<>();
    testMap.put(AdHocSMSData.PHONE_ALIAS, "111222333");
    testMap.put(AdHocSMSData.SMS_TEXT_ALIAS, "test sms msg");
    testMap.put(AdHocSMSData.PARAMETERS_ALIAS, "{}");
    testMap.put(AdHocSMSData.CONTACT_TIME_ALIAS, "15:30");
    testMap.put(AdHocSMSData.CONFIG_ALIAS, "testConfig");

    return testMap;
  }

  private AdHocSMSData buildTestAdHocSMSDataObject() {
    AdHocSMSData adHocSMSData = new AdHocSMSData();
    adHocSMSData.setPhone("555777999");
    adHocSMSData.setSmsText("test message");
    adHocSMSData.setParameters(new HashMap<>());
    adHocSMSData.setContactTime("2022-08-25 13:00");
    adHocSMSData.setConfig("test config");

    return adHocSMSData;
  }
}
