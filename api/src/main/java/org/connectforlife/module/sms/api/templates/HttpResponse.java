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

package org.connectforlife.module.sms.api.templates;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import java.net.HttpURLConnection;

public abstract class HttpResponse {
  protected static final int HTTP_SUCCESS_MIN = HttpURLConnection.HTTP_OK;
  protected static final int HTTP_99 = 99;
  protected static final int HTTP_SUCCESS_MAX = HttpURLConnection.HTTP_OK + HTTP_99;

  /** The success status expected. */
  @JsonProperty protected String successStatus;

  /**
   * Checks whether the given status is a success status.
   *
   * @param status the status to check
   * @return true if this is a success status, false otherwise
   */
  public boolean isSuccessStatus(Integer status) {
    if (StringUtils.isBlank(successStatus)) {
      return (status >= HTTP_SUCCESS_MIN && status <= HTTP_SUCCESS_MAX);
    }
    return status.toString().matches(successStatus);
  }
}
