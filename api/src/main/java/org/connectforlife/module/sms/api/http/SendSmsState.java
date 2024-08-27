/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.util.SMSConstants;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SendSmsState {

  private static final Log LOGGER = LogFactory.getLog(SendSmsState.class);
  private static final long MINUTE = 60000;

  private Template template;
  private HttpClient httpClient;
  private HttpMethod httpMethod;

  private Integer httpStatus;
  private String httpResponse;
  private String errorMessage;

  public Template getTemplate() {
    return template;
  }

  public SendSmsState setTemplate(Template template) {
    this.template = template;
    return this;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public SendSmsState setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
    return this;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public SendSmsState setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
    return this;
  }

  public Integer getHttpStatus() {
    return httpStatus;
  }

  public String getHttpResponse() {
    return httpResponse;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public SendSmsState doRequest() {
    boolean shouldRetry = true;
    int retryCount = 0;

    while (shouldRetry) {
      shouldRetry = false;

      try {
        httpStatus = httpClient.executeMethod(httpMethod);
        httpResponse = httpMethod != null ? httpMethod.getResponseBodyAsString() : null;
      } catch (UnknownHostException e) {
        errorMessage =
            String.format(
                "Network connectivity issues or problem with '%s' template? %s",
                template.getName(), e.toString());
      } catch (IllegalArgumentException | IOException | IllegalStateException e) {
        String msg =
            String.format("Problem with '%s' template? %s", template.getName(), e.toString());

        if (SocketException.class.isAssignableFrom(e.getClass())
            && retryCount < SMSConstants.SMS_DEFAULT_RETRY_COUNT) {
          LOGGER.warn(msg);
          sleep(MINUTE);
          shouldRetry = true;
          retryCount++;
        } else {
          errorMessage = msg;
        }
      } finally {
        if (httpMethod != null) {
          httpMethod.releaseConnection();
        }
      }
    }

    return this;
  }

  private void sleep(long milliseconds) {
    LOGGER.debug(
        String.format(
            "Sleeping thread id %d for %d ms", Thread.currentThread().getId(), milliseconds));
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
