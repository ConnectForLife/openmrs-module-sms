/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Map;

/**
 * The AutomaticResponseData Class.
 *
 * <p>The JSON-serializable class to store complete information about message created by automatic
 * response script.
 *
 * <p>The {@code customParameters} are passed as-is, and their exact meaning depends on the SMS
 * Configuration Template.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutomaticResponseData {
  private String message;
  private Map<String, Object> customParameters;

  /** @return the message text, never null */
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /** @return the Map of custom parameters passed together with massage, never null */
  public Map<String, Object> getCustomParameters() {
    return customParameters;
  }

  public void setCustomParameters(Map<String, Object> customParameters) {
    this.customParameters = customParameters;
  }
}
