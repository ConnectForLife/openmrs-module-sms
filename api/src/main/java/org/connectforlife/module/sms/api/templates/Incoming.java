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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** How providers deal with incoming messages */
public class Incoming {

  /** The key used by the provider to denote the message. */
  @JsonProperty private String messageKey;

  /** The regex pattern used for extracting the message from the data sent by the provider. */
  @JsonProperty private String messageRegex;

  /** The key used by the provider to denote the sender. */
  @JsonProperty private String senderKey;

  /**
   * The regex pattern used for extracting the sender number frm the sender data sent by the
   * provider.
   */
  @JsonProperty private String senderRegex;

  /** The key used by the provider to denote the recipient. */
  @JsonProperty private String recipientKey;

  /**
   * The regex pattern used for extracting the sender number from the sender data sent by the
   * provider.
   */
  @JsonProperty private String recipientRegex;

  /** The key used by the provider to denote the timestamp. */
  @JsonProperty private String timestampKey;

  /** The key used by the provider to denote the message ID. */
  @JsonProperty private String msgIdKey;

  // These patterns are compiled using the regex fields from above
  @JsonIgnore private Pattern extractMessageRegex;
  @JsonIgnore private Pattern extractSenderPattern;
  @JsonIgnore private Pattern extractRecipientPattern;

  public boolean hasMessageRegex() {
    return StringUtils.isNotBlank(messageRegex);
  }

  public String extractMessage(String s) {
    if (extractMessageRegex == null) {
      extractMessageRegex = Pattern.compile(messageRegex);
    }
    Matcher m = extractMessageRegex.matcher(s);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  /**
   * Checks whether a regex pattern for extracting senders is set.
   *
   * @return true if this object has a pattern for extracting senders, false otherwise
   */
  public boolean hasSenderRegex() {
    return senderRegex != null && senderRegex.length() > 0;
  }

  /**
   * Extracts the sender from the given string using the sender regex.
   *
   * @param s the string to parse
   * @return the sender of the message
   */
  public String extractSender(String s) {
    if (extractSenderPattern == null) {
      extractSenderPattern = Pattern.compile(senderRegex);
    }
    Matcher m = extractSenderPattern.matcher(s);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  /**
   * Checks whether a regex pattern for extracting recipients is set.
   *
   * @return true if this object has a pattern for extracting recipients, false otherwise
   */
  public Boolean hasRecipientRegex() {
    return recipientRegex != null && recipientRegex.length() > 0;
  }

  /**
   * Extracts the recipient from the given string using the sender regex.
   *
   * @param s the string to parse
   * @return the recipient of the message
   */
  public String extractRecipient(String s) {
    if (extractRecipientPattern == null) {
      extractRecipientPattern = Pattern.compile(recipientRegex);
    }
    Matcher m = extractRecipientPattern.matcher(s);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  /** @return the key used by the provider to denote the message */
  public String getMessageKey() {
    return messageKey;
  }

  /** @param messageKey the key used by the provider to denote the message */
  public void setMessageKey(String messageKey) {
    this.messageKey = messageKey;
  }

  public String getMessageRegex() {
    return messageRegex;
  }

  public void setMessageRegex(String messageRegex) {
    this.messageRegex = messageRegex;
  }

  /** @return the key used by the provider to denote the sender */
  public String getSenderKey() {
    return senderKey;
  }

  /** @param senderKey the key used by the provider to denote the sender */
  public void setSenderKey(String senderKey) {
    this.senderKey = senderKey;
  }

  public String getSenderRegex() {
    return senderRegex;
  }

  /**
   * @param senderRegex the regex pattern used for extracting the sender number from the sender data
   *     sent by the provider
   */
  public void setSenderRegex(String senderRegex) {
    this.senderRegex = senderRegex;
  }

  /** @return the key used by the provider to denote the recipient */
  public String getRecipientKey() {
    return recipientKey;
  }

  /** @param recipientKey the key used by the provider to denote the recipient */
  public void setRecipientKey(String recipientKey) {
    this.recipientKey = recipientKey;
  }

  /** @return the key used by the provider to denote the timestamp */
  public String getTimestampKey() {
    return timestampKey;
  }

  /** @param timestampKey the key used by the provider to denote the timestamp */
  public void setTimestampKey(String timestampKey) {
    this.timestampKey = timestampKey;
  }

  /** @return the key used by the provider to denote the message ID */
  public String getMsgIdKey() {
    return msgIdKey;
  }

  /** @param msgIdKey the key used by the provider to denote the message ID */
  public void setMsgIdKey(String msgIdKey) {
    this.msgIdKey = msgIdKey;
  }

  public String getRecipientRegex() {
    return recipientRegex;
  }

  /**
   * @param recipientRegex the regex pattern used for extracting the sender number from the sender
   *     data sent by the provider
   */
  public void setRecipientRegex(String recipientRegex) {
    this.recipientRegex = recipientRegex;
  }

  public Pattern getExtractMessageRegex() {
    return extractMessageRegex;
  }

  public void setExtractMessageRegex(Pattern extractMessageRegex) {
    this.extractMessageRegex = extractMessageRegex;
  }

  public Pattern getExtractSenderPattern() {
    return extractSenderPattern;
  }

  public void setExtractSenderPattern(Pattern extractSenderPattern) {
    this.extractSenderPattern = extractSenderPattern;
  }

  public Pattern getExtractRecipientPattern() {
    return extractRecipientPattern;
  }

  public void setExtractRecipientPattern(Pattern extractRecipientPattern) {
    this.extractRecipientPattern = extractRecipientPattern;
  }

  @Override
  public String toString() {
    return "Incoming{"
        + "messageKey='"
        + messageKey
        + '\''
        + ", senderKey='"
        + senderKey
        + '\''
        + ", senderRegex='"
        + senderRegex
        + '\''
        + ", recipientKey='"
        + recipientKey
        + '\''
        + ", recipientRegex='"
        + recipientRegex
        + '\''
        + ", timestampKey='"
        + timestampKey
        + '\''
        + ", msgIdKey='"
        + msgIdKey
        + '\''
        + ", extractSenderPattern="
        + extractSenderPattern
        + ", extractRecipientPattern="
        + extractRecipientPattern
        + '}';
  }
}
