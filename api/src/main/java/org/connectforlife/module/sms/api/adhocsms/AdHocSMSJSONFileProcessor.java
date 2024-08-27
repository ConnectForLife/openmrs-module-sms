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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.connectforlife.module.sms.api.data.AdHocSMSData;

public class AdHocSMSJSONFileProcessor implements AdHocSMSInputSourceProcessor {

  public static final String JSON_FILE_EXTENSION = "json";

  private static final Log LOGGER = LogFactory.getLog(AdHocSMSJSONFileProcessor.class);

  @Override
  public boolean shouldProcessData(AdHocSMSInputSourceProcessorContext context) {
    return JSON_FILE_EXTENSION.equalsIgnoreCase(context.getOptions().get(
        AdHocSMSInputSourceProcessor.EXTENSION_FILE_PROP_NAME));
  }

  @Override
  public List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context) {
    List<AdHocSMSData> adHocSMSData = new ArrayList<>();
    try {
      adHocSMSData = new ObjectMapper().readValue(context.getFile(),
          new TypeReference<List<AdHocSMSData>>() {
          });
    } catch (IOException ex) {
      LOGGER.error("Error while processing JSON file", ex);
    }

    return adHocSMSData;
  }
}
