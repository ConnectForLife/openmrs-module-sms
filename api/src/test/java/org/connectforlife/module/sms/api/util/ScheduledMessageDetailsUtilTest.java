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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.StringJoiner;
import org.junit.Test;
import org.connectforlife.module.sms.api.adhocsms.ScheduledMessageDetails;

public class ScheduledMessageDetailsUtilTest {

  private static final String TEST_JSON_STRING = "{\"name\":\"testName\",\"description\":\"testDescription\",\"reportUuid\":\"a4236158-457e-11ed-b38c-0242ac140002\",\"cronExpression\":\"0 */15 * ? * *\"}";

  @Test
  public void shouldConvertScheduledMessageDetailsObjectToJSONString() {
    ScheduledMessageDetails scheduledMessageDetails = buildScheduledMessageDetailsObject();

    String actual = ScheduledMessageDetailsUtil.toJSONString(scheduledMessageDetails);

    assertNotNull(actual);
    assertEquals(TEST_JSON_STRING, actual);
  }

  @Test
  public void shouldConvertJSONStringToScheduledMessageDetailsObject() {
    ScheduledMessageDetails actual = ScheduledMessageDetailsUtil.fromJSONString(TEST_JSON_STRING);

    assertNotNull(actual);
    assertEquals("testName", actual.getName());
    assertEquals("testDescription", actual.getDescription());
    assertEquals("a4236158-457e-11ed-b38c-0242ac140002", actual.getReportUuid());
    assertEquals("0 */15 * ? * *", actual.getCronExpression());
  }

  @Test
  public void shouldConvertJSONStringToObjectsList() {
    String jsonString = new StringJoiner(" ").add("[").add(TEST_JSON_STRING).add("]").toString();
    List<ScheduledMessageDetails> actual = ScheduledMessageDetailsUtil.convertJSONStringToObjectsList(
        jsonString);

    assertNotNull(actual);
    assertFalse(actual.isEmpty());
    assertEquals(1, actual.size());
  }

  private ScheduledMessageDetails buildScheduledMessageDetailsObject() {
    ScheduledMessageDetails scheduledMessageDetails = new ScheduledMessageDetails();
    scheduledMessageDetails.setName("testName");
    scheduledMessageDetails.setDescription("testDescription");
    scheduledMessageDetails.setReportUuid("a4236158-457e-11ed-b38c-0242ac140002");
    scheduledMessageDetails.setCronExpression("0 */15 * ? * *");

    return scheduledMessageDetails;
  }
}
