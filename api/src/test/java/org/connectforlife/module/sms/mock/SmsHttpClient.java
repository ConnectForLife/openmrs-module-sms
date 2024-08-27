/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.mock;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

public class SmsHttpClient extends HttpClient {
  private static final String MESSAGE_ID_PARAM = ",\"message-id\": \"0A0000000123ABCD1\"\n";
  private boolean successProcessed;
  private String message;
  private boolean messageId;

  public SmsHttpClient() {
    this.messageId = true;
    this.successProcessed = true;
  }

  @Override
  public int executeMethod(HttpMethod method) {
    if (successProcessed) {
      return processWithSuccess(method);
    }
    return processWithFailure(method);
  }

  private int processWithSuccess(HttpMethod method) {
    ReflectionTestUtils.setField(method, "responseBody", getSuccessMessage().getBytes());
    return HttpStatus.SC_OK;
  }

  private String getSuccessMessage() {
    StringBuilder additionalProps = new StringBuilder();
    if (messageId) {
      additionalProps.append(MESSAGE_ID_PARAM);
    }
    return String.format(message, additionalProps.toString());
  }

  private int processWithFailure(HttpMethod method) {
    ReflectionTestUtils.setField(method, "responseBody", message.getBytes());
    return HttpStatus.SC_INTERNAL_SERVER_ERROR;
  }

  public boolean isSuccessProcessed() {
    return successProcessed;
  }

  public SmsHttpClient withSuccessProcessed(boolean successProcessed, String message) {
    this.successProcessed = successProcessed;
    this.message = message;
    return this;
  }

  public boolean isMessageId() {
    return messageId;
  }

  public SmsHttpClient withMessageId(boolean withMessageId) {
    this.messageId = withMessageId;
    return this;
  }
}
