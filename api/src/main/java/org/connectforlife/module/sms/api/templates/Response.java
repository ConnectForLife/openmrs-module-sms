/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.templates;

// todo: handle malformed template files (ie: resulting in exceptions in the regex parsing) in a
// useful way for implementers?

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** How to to deal with provider-specific http responses */
@SuppressWarnings({"PMD.TooManyFields"})
public class Response extends HttpResponse {

  private static final String SEARCH_GROUPS_EXCEPTION_FORMAT =
      "Invalid number of search groups, expected: 1, actual: %s.";

  /** Whether recipients are provided in multiple lines. */
  @JsonProperty private Boolean multiLineRecipientResponse = Boolean.FALSE;

  /** Whether the provider has a different type of response for single recipient messages. */
  @JsonProperty private Boolean singleRecipientResponse = Boolean.FALSE;

  /** If not empty, indicates that the provider sends responses for successful requests. */
  @JsonProperty private String successResponse;

  /** The regex pattern that can be used for extracting ID from single success messages. */
  @JsonProperty private String extractSingleSuccessMessageId;

  /** The regex pattern that can be used for extracting single failure messages. */
  @JsonProperty private String extractSingleFailureMessage;

  /** The regex pattern used for extracting the general failure message. */
  @JsonProperty private String extractGeneralFailureMessage;

  /** The regex pattern used for extracting the success message ID and its recipient. */
  @JsonProperty private String extractSuccessMessageIdAndRecipient;

  /** The regex pattern used for extracting the failure message and its recipient. */
  @JsonProperty private String extractFailureMessageAndRecipient;

  /** The regex pattern used for extracting the raw provider status. Optional. */
  @JsonProperty private String extractProviderStatus;

  /** The name of the HTTP header containing the message ID. */
  @JsonProperty private String headerMessageId;

  // These patterns are compiled from the strings above, when needed
  @JsonIgnore private Pattern successResponsePattern;
  @JsonIgnore private Pattern extractSingleSuccessMessageIdPattern;
  @JsonIgnore private Pattern extractSingleFailureMessagePattern;
  @JsonIgnore private Pattern extractGeneralFailureMessagePattern;
  @JsonIgnore private Pattern extractSuccessMessageIdAndRecipientPattern;
  @JsonIgnore private Pattern extractFailureMessageAndRecipientPattern;
  @JsonIgnore private Pattern extractProviderStatusPattern;

  /** @return true if this provider returns responses for successful requests */
  public Boolean hasSuccessResponse() {
    return StringUtils.isNotBlank(successResponse);
  }

  /**
   * Checks whether the given response is a success response.
   *
   * @param response the response to check
   * @return true if it is a success response, false otherwise
   */
  public Boolean checkSuccessResponse(String response) {
    if (successResponsePattern == null) {
      successResponsePattern = Pattern.compile(successResponse, Pattern.DOTALL);
    }
    Matcher matcher = successResponsePattern.matcher(response);
    return matcher.matches();
  }

  /**
   * @return true if this provider returns recipient responses in multiple lines, false otherwise
   */
  public Boolean supportsMultiLineRecipientResponse() {
    return multiLineRecipientResponse;
  }

  /**
   * @return true if this provider supports a different response type for an SMS sent to a single
   *     recipient, false otherwise
   */
  public Boolean supportsSingleRecipientResponse() {
    return singleRecipientResponse;
  }

  /** @return true if the id for successfully sent messages is included in the response */
  public Boolean hasSingleSuccessMessageId() {
    return StringUtils.isNotBlank(extractSingleSuccessMessageId);
  }

  /**
   * Extracts the single line success message ID from the response.
   *
   * @param response the response to parse
   * @return the extracted ID
   */
  public String extractSingleSuccessMessageId(String response) {
    if (extractSingleSuccessMessageIdPattern == null) {
      extractSingleSuccessMessageIdPattern = Pattern.compile(extractSingleSuccessMessageId);
    }
    Matcher m = extractSingleSuccessMessageIdPattern.matcher(response);
    if (m.groupCount() != 1) {
      throw new IllegalStateException(
          String.format(
              "Template error, extractSingleSuccessMessageId: " + SEARCH_GROUPS_EXCEPTION_FORMAT,
              m.groupCount()));
    }
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  /**
   * Extracts the single failure message from the response.
   *
   * @param response the response to parse
   * @return the failure message
   */
  public String extractSingleFailureMessage(String response) {
    if (extractSingleFailureMessage != null && extractSingleFailureMessage.length() > 0) {
      if (extractSingleFailureMessagePattern == null) {
        extractSingleFailureMessagePattern = Pattern.compile(extractSingleFailureMessage);
      }
      Matcher m = extractSingleFailureMessagePattern.matcher(response);
      if (m.groupCount() != 1) {
        throw new IllegalStateException(
            String.format(
                "Template error, extractSingleFailureMessage: " + SEARCH_GROUPS_EXCEPTION_FORMAT,
                m.groupCount()));
      }
      if (m.find()) {
        return m.group(1);
      }
    }
    return null;
  }

  /**
   * Extracts the general failure message from the response.
   *
   * @param response the response to parse
   * @return the extracted failure message
   */
  public String extractGeneralFailureMessage(String response) {
    if (extractGeneralFailureMessagePattern == null) {
      extractGeneralFailureMessagePattern = Pattern.compile(extractGeneralFailureMessage);
    }
    Matcher m = extractGeneralFailureMessagePattern.matcher(response);
    if (m.groupCount() != 1) {
      throw new IllegalStateException(
          String.format(
              "Template error, extractGeneralFailureMessage: " + SEARCH_GROUPS_EXCEPTION_FORMAT,
              m.groupCount()));
    }
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  // todo: what if bad or wrong number of regex groups ? ie (only one extract)

  /**
   * Extracts the success message ID and recipient from the response.
   *
   * @param response the response to parse
   * @return a string array, where the success message ID is the first element and the recipient is
   *     the second one
   */
  public String[] extractSuccessMessageIdAndRecipient(String response) {
    if (extractSuccessMessageIdAndRecipientPattern == null) {
      extractSuccessMessageIdAndRecipientPattern =
          Pattern.compile(extractSuccessMessageIdAndRecipient);
    }
    Matcher m = extractSuccessMessageIdAndRecipientPattern.matcher(response);
    if (m.groupCount() != 2) {
      throw new IllegalStateException(
          String.format(
              "Template error, extractSuccessMessageIdAndRecipient: "
                  + "Invalid number of search groups, expected: 2, actual: %s.",
              m.groupCount()));
    }
    if (m.find()) {
      return new String[] {m.group(1), m.group(2)};
    }
    return new String[]{} ;
  }

  /**
   * Extracts the failure message and recipient from the response.
   *
   * @param response the response to parse
   * @return a string array, where the failure message is the first element and the recipient is the
   *     second one
   */
  public String[] extractFailureMessageAndRecipient(String response) {
    if (extractFailureMessageAndRecipientPattern == null) {
      extractFailureMessageAndRecipientPattern = Pattern.compile(extractFailureMessageAndRecipient);
    }
    Matcher m = extractFailureMessageAndRecipientPattern.matcher(response);
    if (m.groupCount() != 2) {
      throw new IllegalStateException(
          String.format(
              "Template error, extractFailureMessageAndRecipient: "
                  + "Invalid number of search groups, expected: 2, actual: %s.",
              m.groupCount()));
    }
    if (m.find()) {
      return new String[] {m.group(1), m.group(2)};
    }
    return new String[]{};
  }

  /**
   * Extracts the provider status from the response, based on the defined regex in {@link
   * Response#extractProviderStatus}.
   *
   * @param response the response to parse
   * @return the provider status as string, or null if {@link Response#extractProviderStatus} is not
   *     defined
   */
  public String extractProviderStatus(String response) {
    if (extractProviderStatusPattern == null && StringUtils.isNotBlank(extractProviderStatus)) {
      extractProviderStatusPattern = Pattern.compile(extractProviderStatus);
    }

    String providerStatus = null;

    if (extractProviderStatusPattern != null) {
      Matcher matcher = extractProviderStatusPattern.matcher(response);

      if (matcher.groupCount() != 1) {
        throw new IllegalStateException(
            String.format(
                "Template error, extractProviderStatus: " + SEARCH_GROUPS_EXCEPTION_FORMAT,
                matcher.groupCount()));
      }

      if (matcher.find()) {
        providerStatus = matcher.group(1);
      }
    }

    return providerStatus;
  }

  /** @return true if this provider returns message IDs as a header, false otherwise */
  public boolean hasHeaderMessageId() {
    return StringUtils.isNotBlank(headerMessageId);
  }

  /** @return the header name for the message ID */
  public String getHeaderMessageId() {
    return headerMessageId;
  }

  @Override
  public String toString() {
    return "Response{"
        + "headerMessageId='"
        + headerMessageId
        + '\''
        + ", multiLineRecipientResponse="
        + multiLineRecipientResponse
        + ", singleRecipientResponse="
        + singleRecipientResponse
        + ", successStatus='"
        + successStatus
        + '\''
        + ", successResponse='"
        + successResponse
        + '\''
        + ", extractSingleSuccessMessageId='"
        + extractSingleSuccessMessageId
        + '\''
        + ", extractSingleFailureMessage='"
        + extractSingleFailureMessage
        + '\''
        + ", extractGeneralFailureMessage='"
        + extractGeneralFailureMessage
        + '\''
        + ", extractSuccessMessageIdAndRecipient='"
        + extractSuccessMessageIdAndRecipient
        + '\''
        + ", extractFailureMessageAndRecipient='"
        + extractFailureMessageAndRecipient
        + '\''
        + '}';
  }
}
