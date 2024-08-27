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

package org.connectforlife.module.sms.api.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.templates.Outgoing;

import java.text.MessageFormat;
import java.util.Map;

public class HttpAuthenticationBASICHandler implements HttpAuthenticationHandler {
  private static final Log LOGGER = LogFactory.getLog(HttpAuthenticationBASICHandler.class);

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

  @Override
  public Outgoing.AuthenticationSchema getHandlerSchema() {
    return Outgoing.AuthenticationSchema.BASIC;
  }

  @Override
  public void authenticateHttpMethod(HttpAuthenticationContext authenticationContext) {
    final HttpClient httpClient = authenticationContext.getHttpClient();
    final Map<String, Object> processingProperties =
        authenticationContext.getProcessingProperties();

    if (processingProperties.containsKey(USERNAME) && processingProperties.containsKey(PASSWORD)) {
      String u = processingProperties.get(USERNAME).toString();
      LOGGER.debug(
          MessageFormat.format(
              "BASIC authentication for config {0} and username {1}",
              authenticationContext.getConfig().getName(), u));

      String p = processingProperties.get(PASSWORD).toString();

      httpClient.getParams().setAuthenticationPreemptive(true);
      httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(u, p));
    } else {
      String message;
      if (processingProperties.containsKey(USERNAME)) {
        message =
            String.format(
                "Config %s: missing password", authenticationContext.getConfig().getName());
      } else if (processingProperties.containsKey(PASSWORD)) {
        message =
            String.format(
                "Config %s: missing username", authenticationContext.getConfig().getName());
      } else {
        message =
            String.format(
                "Config %s: missing username and password",
                authenticationContext.getConfig().getName());
      }
      throw new IllegalStateException(message);
    }
  }
}
