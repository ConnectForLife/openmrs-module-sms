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
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.connectforlife.module.sms.mock.MockHttpClient;
import org.connectforlife.module.sms.util.ResourceUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.templates.Template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JWTRepositoryTest {
  private static final String AUTH_REQUEST =
      "{\n"
          + "  \"api_username\": \"74b165e8-a955-4727-854d-0e241ec93adf\",\n"
          + "  \"api_password\": \"be5d7e36-ff9c-4d85-b527-2e4f022ec194\"\n"
          + "}";
  private static final String AUTH_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
          + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
  private static final String AUTH_RESPONSE =
      "{\n"
          + "  \"success\": true,\n"
          + "  \"expires_at\": \"2019-01-01T00:00:00Z\",\n"
          + "  \"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\",\n"
          + "  \"refresh_token\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE1NzA2OTgxODMsImV4cCI6MTYwMjIzNDE4MywiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3\"\n"
          + "}";

  private Template fdiTemplate;
  private Configs fdiConfigs;

  @Before
  public void setup() throws IOException {
    fdiTemplate = ResourceUtil.loadJSON("templates/fdi-template.json", Template.class);
    fdiConfigs = ResourceUtil.loadJSON("configs/fdi-config.json", Configs.class);
  }

  @Test
  public void shouldGenerateCorrectRequest() throws IOException {
    final MockHttpClient mockHttpClient = new MockHttpClient();
    mockHttpClient.addResponse(Pattern.compile("/api/v1/auth.*"), HttpStatus.SC_OK, AUTH_RESPONSE);

    final SingletonHttpClientService singletonHttpClientService = new SingletonHttpClientService();
    singletonHttpClientService.setSingleton(mockHttpClient);

    final JWTRepository jwtRepository = new JWTRepository();
    jwtRepository.setHttpClientService(singletonHttpClientService);

    final HttpMethod httpMethod = Mockito.mock(HttpMethod.class);
    final Map<String, Object> properties = new HashMap<>();
    properties.put("api_username", "74b165e8-a955-4727-854d-0e241ec93adf");
    properties.put("api_password", "be5d7e36-ff9c-4d85-b527-2e4f022ec194");

    final HttpAuthenticationContext context =
        new HttpAuthenticationContext(
            mockHttpClient, httpMethod, fdiConfigs.getDefaultConfig(), fdiTemplate, properties);

    final String token = jwtRepository.getTokenValue(context);
    Assert.assertEquals(AUTH_TOKEN, token);

    final HttpMethod getTokenMethod = mockHttpClient.getExecutedMethods().peekLast();
    Assert.assertNotNull(getTokenMethod);
    Assert.assertEquals(PostMethod.class, getTokenMethod.getClass());
    final RequestEntity getTokenRequestEntity = ((PostMethod) getTokenMethod).getRequestEntity();
    Assert.assertEquals(StringRequestEntity.class, getTokenRequestEntity.getClass());
    final String getTokenRequestContent =
        ((StringRequestEntity) getTokenRequestEntity).getContent();

    final ObjectMapper objectMapper = new ObjectMapper();
    final Map<String, Object> expectedRequestContentMap =
        objectMapper.readValue(AUTH_REQUEST, new TypeReference<Map<String, Object>>() {});
    final Map<String, Object> requestContentMap =
        objectMapper.readValue(getTokenRequestContent, new TypeReference<Map<String, Object>>() {});
    Assert.assertEquals(expectedRequestContentMap, requestContentMap);
  }
}
