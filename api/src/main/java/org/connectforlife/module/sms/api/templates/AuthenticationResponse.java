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

import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.api.APIException;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationResponse extends HttpResponse {

  /** If not empty, indicates that the provider sends responses for successful requests. */
  @JsonProperty private String successResponse;

  /** Regex to extract authentication Token. */
  @JsonProperty private String extractAuthenticationToken;

  @JsonProperty private String extractExpiresAt;

  @JsonProperty private String expiresAtFormat = "yyyy-MM-dd'T'HH:mm:ssVV";

  private Map<String, Pattern> extractResultPatternCache = new ConcurrentHashMap<>();

  public String extractAuthenticationToken(String response) {
    return extractByRegex(extractAuthenticationToken, "Authentication Token", response);
  }

  public ZonedDateTime extractExpiresAt(String response) {
    final String expiresAtText = extractByRegex(extractExpiresAt, "Expires At", response);

    try {
      return ZonedDateTime.parse(expiresAtText, DateTimeFormatter.ofPattern(expiresAtFormat));
    } catch (DateTimeParseException dtpe) {
      throw new APIException(
          MessageFormat.format(
              "Failed to parse Expires At date time from raw value: {0} using date time "
                  + "pattern: {1}",
              expiresAtText, expiresAtFormat),
          dtpe);
    }
  }

  private String extractByRegex(String regex, String propertyName, String response) {
    final Pattern propertyPattern =
        extractResultPatternCache.computeIfAbsent(propertyName, key -> Pattern.compile(regex));

    try {
      Matcher m = propertyPattern.matcher(response);
      if (m.find()) {
        return m.group(1);
      }
    } catch (Exception e) {
      throw new APIException(getExtractByRegexError(regex, propertyName, response), e);
    }

    throw new APIException(getExtractByRegexError(regex, propertyName, response));
  }

  private String getExtractByRegexError(String regex, String propertyName, String response) {
    return MessageFormat.format(
        "Failed to extract {0} using regex: {1} from response: {2}", propertyName, regex, response);
  }

  public String getSuccessResponse() {
    return successResponse;
  }

  public void setSuccessResponse(String successResponse) {
    this.successResponse = successResponse;
  }

  public String getExtractAuthenticationToken() {
    return extractAuthenticationToken;
  }

  public void setExtractAuthenticationToken(String extractAuthenticationToken) {
    this.extractAuthenticationToken = extractAuthenticationToken;
  }

  public String getExtractExpiresAt() {
    return extractExpiresAt;
  }

  public void setExtractExpiresAt(String extractExpiresAt) {
    this.extractExpiresAt = extractExpiresAt;
  }

  public String getExpiresAtFormat() {
    return expiresAtFormat;
  }

  public void setExpiresAtFormat(String expiresAtFormat) {
    this.expiresAtFormat = expiresAtFormat;
  }
}
