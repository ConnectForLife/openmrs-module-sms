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

import java.io.InputStream;
import java.util.Map;

/**
 * Context class for ad hoc SMS input source processors
 */
public class AdHocSMSInputSourceProcessorContext {

  private InputStream file;

  private Map<String, String> options;

  public AdHocSMSInputSourceProcessorContext(InputStream file, Map<String, String> options) {
    this.file = file;
    this.options = options;
  }

  public InputStream getFile() {
    return file;
  }

  public void setFile(InputStream file) {
    this.file = file;
  }

  public Map<String, String> getOptions() {
    return options;
  }

  public void setOptions(Map<String, String> options) {
    this.options = options;
  }
}
