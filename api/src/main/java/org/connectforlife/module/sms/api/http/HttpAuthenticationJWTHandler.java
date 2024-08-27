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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.templates.Outgoing;

import javax.ws.rs.core.HttpHeaders;
import java.text.MessageFormat;

public class HttpAuthenticationJWTHandler implements HttpAuthenticationHandler {
  private static final Log LOGGER = LogFactory.getLog(HttpAuthenticationJWTHandler.class);
  private JWTRepository jwtRepository;

  @Override
  public Outgoing.AuthenticationSchema getHandlerSchema() {
    return Outgoing.AuthenticationSchema.JWT;
  }

  @Override
  public void authenticateHttpMethod(HttpAuthenticationContext authenticationContext) {
    LOGGER.debug(
        MessageFormat.format(
            "JWT authentication for config: {0}", authenticationContext.getConfig().getName()));

    final String jwtValue = jwtRepository.getTokenValue(authenticationContext);
    authenticationContext
        .getHttpMethod()
        .setRequestHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtValue);
  }

  public void setJwtRepository(JWTRepository jwtRepository) {
    this.jwtRepository = jwtRepository;
  }
}
