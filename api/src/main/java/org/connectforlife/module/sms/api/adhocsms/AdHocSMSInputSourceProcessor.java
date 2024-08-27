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

import java.util.List;
import org.connectforlife.module.sms.api.data.AdHocSMSData;

/**
 * Common interface for ad hoc SMS input source processors
 */
public interface AdHocSMSInputSourceProcessor {

  String EXTENSION_FILE_PROP_NAME = "fileExtension";

  /**
   * Checks if ad hoc SMS data should be fetched based on context data
   *
   * @param context input source context data
   * @return true/false
   */
  boolean shouldProcessData(AdHocSMSInputSourceProcessorContext context);

  /**
   * Gets ad hoc SMS data based on context data
   *
   * @param context input source context data
   * @return list of {@link AdHocSMSData} objects
   */
  List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context);

}
