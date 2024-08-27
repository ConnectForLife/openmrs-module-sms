/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 *
 */

package org.connectforlife.module.sms.util;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceUtil {
  public static <T> T loadJSON(String resourceName, Class<T> type) throws IOException {
    final String resourceContent = loadResource(resourceName);
    final ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(resourceContent, type);
  }

  public static String loadResource(String resourceName) throws IOException {
    try (InputStream responseStream =
        ResourceUtil.class.getClassLoader().getResourceAsStream(resourceName)) {
      if (responseStream == null) {
        throw new IOException("Cannot read response file: " + resourceName);
      }
      return IOUtils.toString(responseStream, StandardCharsets.UTF_8);
    }
  }
}
