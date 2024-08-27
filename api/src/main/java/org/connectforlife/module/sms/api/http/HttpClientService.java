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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.connectforlife.module.sms.api.configs.Config;

import java.util.HashMap;
import java.util.Map;

public class HttpClientService {
  private final HttpClient commonHttpClient;
  private final Map<String, HttpClient> smsProviderHttpClients = new HashMap<>();

  HttpClientService(HttpClient commonHttpClient) {
    this.commonHttpClient = commonHttpClient;
  }

  public HttpClientService() {
    this(createDefaultHttpClient());
  }

  private static HttpClient createDefaultHttpClient() {
    return new HttpClient(new MultiThreadedHttpConnectionManager());
  }

  public HttpClient getCommonHttpClient() {
    return commonHttpClient;
  }

  public HttpClient getProviderHttpClient(Config config) {
    return smsProviderHttpClients.computeIfAbsent(
        config.getName(), key -> createDefaultHttpClient());
  }
}
