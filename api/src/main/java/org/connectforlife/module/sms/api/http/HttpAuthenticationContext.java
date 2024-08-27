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
import org.apache.commons.httpclient.HttpMethod;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.templates.Template;

import java.util.Map;

public class HttpAuthenticationContext {
  private final HttpClient httpClient;
  private final HttpMethod httpMethod;
  private final Config config;
  private final Template template;
  private final Map<String, Object> processingProperties;

  public HttpAuthenticationContext(
      HttpClient httpClient,
      HttpMethod httpMethod,
      Config config,
      Template template,
      Map<String, Object> processingProperties) {
    this.httpClient = httpClient;
    this.httpMethod = httpMethod;
    this.config = config;
    this.template = template;
    this.processingProperties = processingProperties;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public Config getConfig() {
    return config;
  }

  public Template getTemplate() {
    return template;
  }

  public Map<String, Object> getProcessingProperties() {
    return processingProperties;
  }
}
