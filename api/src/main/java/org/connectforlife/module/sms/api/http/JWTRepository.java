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

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.templates.AuthenticationResponse;
import org.connectforlife.module.sms.api.util.OutgoingRequestBuilder;
import org.openmrs.api.APIException;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JWTRepository {
  private static final Log LOGGER = LogFactory.getLog(JWTRepository.class);
  private static final long EXPIRE_DATE_TIME_CHECK_OFFSET_SECONDS = 16;

  private final Clock clock;
  private final Map<String, Token> tokens = new ConcurrentHashMap<>();

  private HttpClientService httpClientService;

  JWTRepository(Clock clock) {
    this.clock = clock;
  }

  public JWTRepository() {
    this(Clock.systemDefaultZone());
  }

  public void setHttpClientService(HttpClientService httpClientService) {
    this.httpClientService = httpClientService;
  }

  public String getTokenValue(HttpAuthenticationContext authenticationContext) {
    LOGGER.debug(
        MessageFormat.format(
            "Requesting token for config {0}.", authenticationContext.getConfig().getName()));
    return tokens
        .compute(
            authenticationContext.getConfig().getName(),
            (key, token) -> computeToken(authenticationContext, token))
        .getValue();
  }

  private Token computeToken(HttpAuthenticationContext authenticationContext, Token currentToken) {
    if (currentToken != null
        && currentToken
            .getExpireAt()
            .isAfter(ZonedDateTime.now(clock).plusSeconds(EXPIRE_DATE_TIME_CHECK_OFFSET_SECONDS))) {
      LOGGER.debug(
          MessageFormat.format(
              "Found valid token for {0}, expires at: {1}, token: {2}",
              authenticationContext.getConfig().getName(),
              currentToken.getExpireAt(),
              abbreviateTokenForLog(currentToken.getValue())));
      return currentToken;
    } else {
      LOGGER.debug("Generating new token for " + authenticationContext.getConfig().getName());
      return generateNewToken(authenticationContext);
    }
  }

  private Token generateNewToken(HttpAuthenticationContext authenticationContext) {
    final HttpMethod getTokenMethod =
        new OutgoingRequestBuilder()
            .withTemplateName(authenticationContext.getTemplate().getName())
            .withOutgoingRequest(
                authenticationContext.getTemplate().getOutgoing().getAuthentication().getRequest())
            .withProps(authenticationContext.getProcessingProperties())
            .build();

    try {
      final AuthenticationResponse authenticationResponseConfig =
          authenticationContext.getTemplate().getOutgoing().getAuthentication().getResponse();

      int resultStatus = httpClientService.getCommonHttpClient().executeMethod(getTokenMethod);

      if (authenticationResponseConfig.isSuccessStatus(resultStatus)) {
        final String resultBody = getTokenMethod.getResponseBodyAsString();

        String tokenValue = authenticationResponseConfig.extractAuthenticationToken(resultBody);
        ZonedDateTime expiresAt = authenticationResponseConfig.extractExpiresAt(resultBody);
        LOGGER.debug(
            MessageFormat.format(
                "Generated token for {0}, expires at: {1}, token: {2}",
                authenticationContext.getConfig().getName(),
                expiresAt,
                abbreviateTokenForLog(tokenValue)));
        return new Token(tokenValue, expiresAt);
      }

      throw new APIException(
          MessageFormat.format(
              "Auth Token request failed with HTTP code: {0}, body: {1}",
              resultStatus, getTokenMethod.getResponseBodyAsString()));
    } catch (IOException ioe) {
      throw new APIException(
          MessageFormat.format("Failed to make Auth Token request {0}", getTokenMethod.getPath()),
          ioe);
    } finally {
      getTokenMethod.releaseConnection();
    }
  }

  private String abbreviateTokenForLog(String tokenValue) {
    return StringUtils.abbreviate(tokenValue, 8);
  }

  private static class Token {
    final String value;
    final ZonedDateTime expireAt;

    Token(String value, ZonedDateTime expireAt) {
      this.value = value;
      this.expireAt = expireAt;
    }

    String getValue() {
      return value;
    }

    ZonedDateTime getExpireAt() {
      return expireAt;
    }
  }
}
