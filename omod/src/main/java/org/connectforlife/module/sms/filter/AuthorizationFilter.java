/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.filter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Filter intended for all /ws/sms calls that allows the user to authenticate via Basic
 * authentication. (It will not fail on invalid or missing credentials. We count on the API to throw
 * exceptions if an unauthenticated user tries to do something they are not allowed to do.)
 */
public class AuthorizationFilter implements Filter {

  private static final Log LOGGER = LogFactory.getLog(AuthorizationFilter.class);

  private static final String BASIC_KEYWORD = "Basic ";

  private static final String[] EXCLUDED_URIS = {"/ws/sms/incoming", "/ws/sms/status"};

  /** @see javax.servlet.Filter#init(javax.servlet.FilterConfig) */
  @Override
  public void init(FilterConfig arg0) throws ServletException {
    LOGGER.debug("Initializing SMS Authorization filter");
  }

  /** @see javax.servlet.Filter#destroy() */
  @Override
  public void destroy() {
    LOGGER.debug("Destroying SMS Authorization filter");
  }

  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
   *     javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;

      if (isNotExcludedURI(httpRequest)) {
        performSMSAuthCheck(httpRequest, (HttpServletResponse) response);
      }
    }

    chain.doFilter(request, response);
  }

  private boolean isNotExcludedURI(HttpServletRequest httpServletRequest) {
    for (String excludedURI : EXCLUDED_URIS) {
      if (httpServletRequest.getRequestURI().toLowerCase().contains(excludedURI)) {
        return false;
      }
    }

    return true;
  }

  private void performSMSAuthCheck(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
      throws IOException {
    // skip if the session has timed out, we're already authenticated, or it's not an HTTP
    // request
    if (!httpRequest.isRequestedSessionIdValid()) {
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Session timed out");
    }
    String authorization = httpRequest.getHeader("Authorization");
    if (authorization != null && authorization.contains(BASIC_KEYWORD)) {
      performBasicAuth(authorization);
    }
  }

  private void performBasicAuth(String authorization) {
    // this is "Basic ${base64encode(username + ":" + password)}"
    try {
      String decoded =
          new String(
              Base64.decodeBase64(authorization.replace(BASIC_KEYWORD, "")),
              StandardCharsets.UTF_8);
      String[] userAndPass = decoded.split(":");
      Context.authenticate(userAndPass[0], userAndPass[1]);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("authenticated " + userAndPass[0]);
      }
    } catch (ContextAuthenticationException ex) {
      Context.logout();
    } catch (Exception ex) {
      // This filter never stops execution. If the user failed to
      // authenticate, that will be caught later.
      LOGGER.error("Authentication failure");
    }
  }
}
