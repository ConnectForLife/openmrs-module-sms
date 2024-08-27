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
import org.openmrs.api.context.Context;

import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Map;

public class HttpAuthenticationService {
  private static final Log LOGGER = LogFactory.getLog(HttpAuthenticationService.class);

  private final Map<Outgoing.AuthenticationSchema, HttpAuthenticationHandler>
      authenticationHandlers = new EnumMap<>(Outgoing.AuthenticationSchema.class);

  public void authenticate(HttpAuthenticationContext authenticationContext) {
    final Outgoing outgoingRequestTemplate = authenticationContext.getTemplate().getOutgoing();

    if (Boolean.TRUE.equals(outgoingRequestTemplate.hasAuthentication())) {
      LOGGER.debug(
          MessageFormat.format(
              "Authenticating request for config: {0}, authentication type: {1}",
              authenticationContext.getConfig().getName(),
              authenticationContext.getTemplate().getOutgoing().getAuthenticationSchema()));

      if (!getAuthenticationHandlers()
          .containsKey(outgoingRequestTemplate.getAuthenticationSchema())) {
        throw new IllegalStateException(
            MessageFormat.format(
                "Unknown Authentication Schema {0} used in template {1}.",
                outgoingRequestTemplate.getAuthenticationSchema(),
                authenticationContext.getTemplate().getName()));
      }

      getAuthenticationHandlers()
          .get(outgoingRequestTemplate.getAuthenticationSchema())
          .authenticateHttpMethod(authenticationContext);
    }
  }

  private Map<Outgoing.AuthenticationSchema, HttpAuthenticationHandler>
      getAuthenticationHandlers() {
    synchronized (authenticationHandlers) {
      if (authenticationHandlers.isEmpty()) {
        Context.getRegisteredComponents(HttpAuthenticationHandler.class)
            .forEach(handler -> authenticationHandlers.put(handler.getHandlerSchema(), handler));
      }

      return authenticationHandlers;
    }
  }
}
