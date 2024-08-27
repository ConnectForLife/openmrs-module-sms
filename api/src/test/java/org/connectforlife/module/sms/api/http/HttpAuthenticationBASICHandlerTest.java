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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.templates.Template;

import java.util.HashMap;
import java.util.Map;

public class HttpAuthenticationBASICHandlerTest {
  @Test
  public void testAuthenticateBASIC() {
    final String username = "Username1";
    final String password = "Password";

    final HttpState httpState = Mockito.mock(HttpState.class);
    final HttpClient httpClient = Mockito.mock(HttpClient.class);
    Mockito.when(httpClient.getParams()).thenReturn(Mockito.mock(HttpClientParams.class));
    Mockito.when(httpClient.getState()).thenReturn(httpState);

    final HttpMethod httpMethod = Mockito.mock(HttpMethod.class);
    final Config config = Mockito.mock(Config.class);
    final Template template = Mockito.mock(Template.class);
    final Map<String, Object> properties = new HashMap<>();
    properties.put("username", "Username1");
    properties.put("password", "Password");

    final HttpAuthenticationContext context =
        new HttpAuthenticationContext(httpClient, httpMethod, config, template, properties);
    final HttpAuthenticationBASICHandler handler = new HttpAuthenticationBASICHandler();

    handler.authenticateHttpMethod(context);

    final ArgumentCaptor<Credentials> credentialsCaptor = ArgumentCaptor.forClass(Credentials.class);
    Mockito.verify(httpState).setCredentials(Mockito.any(AuthScope.class), credentialsCaptor.capture());
    final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) credentialsCaptor.getValue();
    Assert.assertEquals(username, credentials.getUserName());
    Assert.assertEquals(password, credentials.getPassword());
  }
}
