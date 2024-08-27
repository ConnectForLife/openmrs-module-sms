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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.templates.Template;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

public class HttpAuthenticationJWTHandlerTest {
  @Test
  public void testAuthenticateJWT() {
    final String jwtValue = "028b8a8e-56a3-4dcf-bd7b-49801236d89e";
    final String expectedHeader = "Bearer " + jwtValue;
    final HttpClient httpClient = Mockito.mock(HttpClient.class);
    final HttpMethod httpMethod = Mockito.mock(HttpMethod.class);
    final Config config = Mockito.mock(Config.class);
    final Template template = Mockito.mock(Template.class);
    final Map<String, Object> properties = new HashMap<>();
    final JWTRepository jwtRepository = Mockito.mock(JWTRepository.class);

    final HttpAuthenticationContext context =
        new HttpAuthenticationContext(httpClient, httpMethod, config, template, properties);

    Mockito.when(jwtRepository.getTokenValue(context)).thenReturn(jwtValue);

    final HttpAuthenticationJWTHandler handler = new HttpAuthenticationJWTHandler();
    handler.setJwtRepository(jwtRepository);

    handler.authenticateHttpMethod(context);

    final ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(httpMethod)
        .setRequestHeader(headerNameCaptor.capture(), headerValueCaptor.capture());

    Assert.assertEquals(HttpHeaders.AUTHORIZATION, headerNameCaptor.getValue());
    Assert.assertEquals(expectedHeader, headerValueCaptor.getValue());
  }
}
