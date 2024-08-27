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

import java.io.IOException;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.connectforlife.module.sms.api.adhocsms.ScheduledMessageDetails;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;

public final class ScheduledMessageDetailsUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static String toJSONString(ScheduledMessageDetails value) {
    try {
      return OBJECT_MAPPER.writeValueAsString(value);
    } catch (IOException ex) {
      throw new SmsRuntimeException("Failed to write ScheduledMessageDetails to JSON string", ex);
    }
  }

  public static ScheduledMessageDetails fromJSONString(String jsonString) {
    try {
      return OBJECT_MAPPER.readValue(jsonString, ScheduledMessageDetails.class);
    } catch (IOException ex) {
      throw new SmsRuntimeException("Failed to read ScheduledMessageDetails from JSON string", ex);
    }
  }

  public static List<ScheduledMessageDetails> convertJSONStringToObjectsList(String jsonString) {
    try {
      return OBJECT_MAPPER.readValue(jsonString,
          new TypeReference<List<ScheduledMessageDetails>>() {
          });
    } catch (IOException ex) {
      throw new SmsRuntimeException(
          String.format("Error while parsing JSON string GP: %s", jsonString), ex);
    }
  }

  private ScheduledMessageDetailsUtil() {
  }
}
