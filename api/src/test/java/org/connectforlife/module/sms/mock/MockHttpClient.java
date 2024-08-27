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

package org.connectforlife.module.sms.mock;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

public class MockHttpClient extends HttpClient {
  private final Map<Pattern, Response> responses = new HashMap<>();
  private final List<HttpMethod> executedMethods = new ArrayList<>();

  @Override
  public int executeMethod(HttpMethod method) {
    executedMethods.add(method);

    for (Map.Entry<Pattern, Response> response : responses.entrySet()) {
      if (response.getKey().matcher(method.getPath()).matches()) {
        ReflectionTestUtils.setField(
            method,
            "responseBody",
            response.getValue().getContent().getBytes(StandardCharsets.UTF_8));
        return response.getValue().getStatus();
      }
    }

    throw new IllegalStateException("Response not configured for path: " + method.getPath());
  }

  public Deque<HttpMethod> getExecutedMethods() {
    return new LinkedList<>(executedMethods);
  }

  public void addResponse(Pattern pathPattern, int statusCode, String responseContent) {
    responses.put(pathPattern, new Response(responseContent, statusCode));
  }

  private static class Response {
    final String content;
    final int status;

    Response(String content, int status) {
      this.content = content;
      this.status = status;
    }

    String getContent() {
      return content;
    }

    int getStatus() {
      return status;
    }
  }
}
