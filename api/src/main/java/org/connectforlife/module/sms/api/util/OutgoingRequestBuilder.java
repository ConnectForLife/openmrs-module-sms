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

package org.connectforlife.module.sms.api.util;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.connectforlife.module.sms.api.templates.HttpMethodType;
import org.connectforlife.module.sms.api.templates.Request;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutgoingRequestBuilder {
  private static final String FORM_URLENCODED_WITH_CHARSET_UTF_8 =
      "application/x-www-form-urlencoded; charset=utf-8";

  private String templateName;
  private Request outgoingRequest;
  private Map<String, Object> props;

  public OutgoingRequestBuilder withTemplateName(String templateName) {
    this.templateName = templateName;
    return this;
  }

  public OutgoingRequestBuilder withOutgoingRequest(Request outgoingRequest) {
    this.outgoingRequest = outgoingRequest;
    return this;
  }

  public OutgoingRequestBuilder withProps(Map<String, Object> props) {
    this.props = props;
    return this;
  }

  public HttpMethod build() {
    final HttpMethod httpMethod;

    if (HttpMethodType.POST.equals(outgoingRequest.getType())) {
      httpMethod = generatePOSTRequest();
    } else {
      httpMethod = new GetMethod(outgoingRequest.getUrlPath(props));
    }

    overrideUserAgent(httpMethod);
    httpMethod.setQueryString(addQueryParameters());
    return httpMethod;
  }

  private PostMethod generatePOSTRequest() {
    final PostMethod postMethod = new PostMethod(outgoingRequest.getUrlPath(props));

    if (Boolean.TRUE.equals(outgoingRequest.getJsonContentType())) {
      final Map<String, String> jsonParams = getJsonParameters(outgoingRequest.getBodyParameters());

      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode requestBody = objectMapper.getNodeFactory().objectNode();

      for (Map.Entry<String, String> jsonParam : jsonParams.entrySet()) {
        JsonNode jsonParamNode;
        try {
          jsonParamNode = objectMapper.readTree(jsonParam.getValue());
        } catch (JsonSyntaxException | IOException e) {
          jsonParamNode = objectMapper.getNodeFactory().textNode(jsonParam.getValue());
        }
        requestBody.put(jsonParam.getKey(), jsonParamNode);
      }

      String json = requestBody.toString();
      StringRequestEntity requestEntity;

      try {
        requestEntity = new StringRequestEntity(json, MediaType.APPLICATION_JSON, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException(
            String.format("Template error: %s: invalid json", templateName), e);
      }

      postMethod.setRequestEntity(requestEntity);
    } else {
      postMethod.setRequestHeader("Content-Type", FORM_URLENCODED_WITH_CHARSET_UTF_8);
      addBodyParameters(postMethod);
    }

    addHeaderParameters(postMethod);
    return postMethod;
  }

  private NameValuePair[] addQueryParameters() {
    Map<String, String> queryParameters = outgoingRequest.getQueryParameters();
    List<NameValuePair> queryStringValues = new ArrayList<>(queryParameters.size());
    for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
      String value = TemplatePlaceholderReplaceUtil.placeholderOrLiteral(entry.getValue(), props);
      queryStringValues.add(new NameValuePair(entry.getKey(), value));
    }
    return queryStringValues.toArray(new NameValuePair[0]);
  }

  private Map<String, String> getJsonParameters(Map<String, String> bodyParameters) {
    final Map<String, String> result = new HashMap<>();

    for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
      final String bodyParametersName = entry.getKey();
      if (TemplateOptionalParameterUtil.shouldParameterBeIncluded(bodyParametersName, props)) {
        final String value =
            TemplatePlaceholderReplaceUtil.placeholderOrLiteral(entry.getValue(), props);
        result.put(TemplateOptionalParameterUtil.trimOptionalExpression(bodyParametersName), value);
      }
    }

    return result;
  }

  private void addBodyParameters(PostMethod postMethod) {
    Map<String, String> bodyParameters = outgoingRequest.getBodyParameters();
    for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
      String value = TemplatePlaceholderReplaceUtil.placeholderOrLiteral(entry.getValue(), props);
      postMethod.setParameter(entry.getKey(), value);
    }
  }

  private void addHeaderParameters(HttpMethod httpMethod) {
    Map<String, String> headerParameters = outgoingRequest.getHeaderParameters();
    for (Map.Entry<String, String> entry : headerParameters.entrySet()) {
      String value = TemplatePlaceholderReplaceUtil.placeholderOrLiteral(entry.getValue(), props);
      httpMethod.setRequestHeader(entry.getKey(), value);
    }
  }

  private void overrideUserAgent(HttpMethod httpMethod) {
    if (StringUtils.isNotBlank(outgoingRequest.getOverrideUserAgent())) {
      httpMethod.setRequestHeader(HttpHeaders.USER_AGENT, outgoingRequest.getOverrideUserAgent());
    }
  }
}
