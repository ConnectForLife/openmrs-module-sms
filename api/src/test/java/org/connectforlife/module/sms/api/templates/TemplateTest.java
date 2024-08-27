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

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TemplateTest {

  private static final String PROVIDER_STATUS_TEMPLATE = "templates/provider-status-template.json";
  private static final String NEXMO_RESPONSE_JSON = "responses/nexmo-response.json";

  private static final String TURN_IO_TEMPLATE = "templates/turnIO-template.json";
  private static final String TURN_IO_RESPONSE_JSON = "responses/turnIO-response.json";

  private static final String NEXMO_WHATSAPP_TEXT_MESSAGE_TEMPLATE =
      "templates/nexmo-WhatsApp-text.json";
  private static final String NEXMO_WHATSAPP_TEXT_MESSAGE_REQUEST =
      "requests/nexmo-WhatsApp-text-request.json";

  private static final String NEXMO_WHATSAPP_TEMPLATE_MESSAGE_TEMPLATE =
      "templates/nexmo-WhatsApp-template.json";
  private static final String NEXMO_WHATSAPP_TEMPLATE_MESSAGE_REQUEST =
      "requests/nexmo-WhatsApp-template-request.json";

  private static final String TURNIO_WHATSAPP_TEMPLATE_MESSAGE_TEMPLATE =
      "templates/turnIo-WhatsApp-template.json";
  private static final String TURNIO_WHATSAPP_TEMPLATE_MESSAGE_REQUEST =
      "requests/turnIo-WhatsApp-template-request.json";

  private static final String NEXMO_WHATSAPP_COMBINED_MESSAGE_TEMPLATE =
      "templates/nexmo-WhatsApp-combined.json";

  private static final String NEXMO_WHATSAPP_FAILOVER_MESSAGE_TEMPLATE =
      "templates/nexmo-WhatsApp-failover.json";
  private static final String NEXMO_WHATSAPP_FAILOVER_MESSAGE_REQUEST =
      "requests/nexmo-WhatsApp-failover.json";

  private static final String FDI_MESSAGE_TEMPLATE = "templates/fdi-template.json";
  private static final String FDI_MESSAGE_REQUEST = "requests/fdi-template-request.json";

  private static final String TURN_IO_PROVIDER_ID = "gBEGkYiEB1VXAglK1ZE_qA1YKPrU";

  @Test
  public void shouldGeneratePostMethodForVotoTemplate() throws IOException {
    Template template = loadVotoTemplate();

    String expectedJson =
        "{\"status_callback_url\":\"http://:someUrl\",\"subscribers\":[{\"phone\":\"48700123123\",\"language\":\"en\"}]}";

    Map<String, Object> props = new HashMap<>();
    props.put("message", "message");
    props.put("recipients", "1");
    props.put("callback", "http://:someUrl");
    props.put("subscribers", "[{\"phone\":\"48700123123\",\"language\":\"en\"}]");

    HttpMethod httpMethod = template.generateRequestFor(props);

    final String json = extractRequestBodyJson(httpMethod);
    assertEquals(expectedJson, json);
  }

  @Test
  public void shouldGeneratePostMethodForNexmoWhatsAppTextMessageTemplate() throws IOException {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("from", "500123123");
    properties.put("recipients", "600123123");
    properties.put("message", "Hello World!");

    testRequestBodyGeneration(
        NEXMO_WHATSAPP_TEXT_MESSAGE_TEMPLATE, NEXMO_WHATSAPP_TEXT_MESSAGE_REQUEST, properties);
  }

  @Test
  public void shouldGeneratePostMethodForNexmoWhatsAppTemplateMessageTemplate() throws IOException {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("from", "500123123");
    properties.put("recipients", "600123123");
    properties.put("message", "namespace_uuid:testTemplate");
    properties.put("parameterValues", Arrays.asList("ParamValue1", "ParamValue2"));

    testRequestBodyGeneration(
        NEXMO_WHATSAPP_TEMPLATE_MESSAGE_TEMPLATE,
        NEXMO_WHATSAPP_TEMPLATE_MESSAGE_REQUEST,
        properties);
  }

  @Test
  public void shouldGeneratePostMethodForTurnIOWhatsAppTemplateMessageTemplate()
      throws IOException {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("authorization", "Basic ABC:123");
    properties.put("from", "500123123");
    properties.put("recipients", "600123123");
    properties.put("namespace", "namespace_uuid");
    properties.put("message", "testTemplate");
    properties.put("language", "en_EN");
    properties.put("parameters", "{ \"default\": \"ParamValue1\" }");

    testRequestBodyGeneration(
        TURNIO_WHATSAPP_TEMPLATE_MESSAGE_TEMPLATE,
        TURNIO_WHATSAPP_TEMPLATE_MESSAGE_REQUEST,
        properties);
  }

  @Test
  public void shouldGeneratePostMethodForWhatsAppCombinedTemplateForText() throws IOException {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("from", "500123123");
    properties.put("recipients", "600123123");
    properties.put("message", "Hello World!");

    testRequestBodyGeneration(
        NEXMO_WHATSAPP_COMBINED_MESSAGE_TEMPLATE, NEXMO_WHATSAPP_TEXT_MESSAGE_REQUEST, properties);
  }

  @Test
  public void shouldGeneratePostMethodForWhatsAppCombinedTemplateForTemplate() throws IOException {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("from", "500123123");
    properties.put("recipients", "600123123");
    properties.put("message", "namespace_uuid:testTemplate");
    properties.put("parameterValues", Arrays.asList("ParamValue1", "ParamValue2"));

    testRequestBodyGeneration(
        NEXMO_WHATSAPP_COMBINED_MESSAGE_TEMPLATE,
        NEXMO_WHATSAPP_TEMPLATE_MESSAGE_REQUEST,
        properties);
  }

  @Test
  public void shouldExtractProviderStatus() throws IOException {
    Template template = loadTemplate(PROVIDER_STATUS_TEMPLATE);
    String response = loadResponse(NEXMO_RESPONSE_JSON);

    String providerStatus = template.getOutgoing().getResponse().extractProviderStatus(response);

    assertEquals("0", providerStatus);
  }

  @Test
  public void shouldExtractNullIfProviderStatusNotSet() throws IOException {
    Template template = loadVotoTemplate();
    String response = loadResponse(NEXMO_RESPONSE_JSON);

    String providerStatus = template.getOutgoing().getResponse().extractProviderStatus(response);

    assertNull(providerStatus);
  }

  @Test
  public void shouldExtractProviderIDForTurnIOWithUnderscore() throws IOException {
    Template template = loadTemplate(TURN_IO_TEMPLATE);
    String response = loadResponse(TURN_IO_RESPONSE_JSON);

    String providerID =
        template.getOutgoing().getResponse().extractSingleSuccessMessageId(response);

    assertEquals(TURN_IO_PROVIDER_ID, providerID);
  }

  @Test
  public void shouldGenerateJSONForNexmoFailoverWorkflow() throws IOException {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("from", "12012000000");
    properties.put("failover_from", "48573000000");
    properties.put("recipients", "48603000000");
    properties.put("locale", "en-US");
    properties.put("message", "9c34314d-0663-4e14-bcf7-482cedc7d4e9:sample_shipping_confirmation");
    properties.put(
        "failover_message",
        "Your package has been shipped. It will be delivered in 3 business days.");
    properties.put("parameterValues", Collections.singletonList("3"));

    testRequestBodyGeneration(
        NEXMO_WHATSAPP_FAILOVER_MESSAGE_TEMPLATE,
        NEXMO_WHATSAPP_FAILOVER_MESSAGE_REQUEST,
        properties);
  }

  @Test
  public void shouldGenerateJSONForFutureDynamicInnovations() throws IOException {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("from", "FDI");
    properties.put("recipients", "250780123456");
    properties.put("message", "This is a message");
    properties.put("callback", "https://examples.com/callback.php");
    properties.put("openMrsId", "30bb083a-ae95-43b9-8ed5-051693d018af");

    testRequestBodyGeneration(FDI_MESSAGE_TEMPLATE, FDI_MESSAGE_REQUEST, properties);
  }

  private void testRequestBodyGeneration(
      String templateFileName, String expectedRequestBodyFileName, Map<String, Object> properties)
      throws IOException {
    // given
    final ObjectMapper objectMapper = new ObjectMapper();
    final Template template = loadTemplate(templateFileName);
    final String expectedRequestBody = loadResponse(expectedRequestBodyFileName);

    // when
    final HttpMethod generatedRequest = template.generateRequestFor(properties);
    final String generatedRequestJson = extractRequestBodyJson(generatedRequest);

    // then
    final Map expectedRequestJsonMap = objectMapper.readValue(expectedRequestBody, Map.class);
    final Map generatedRequestJsonMap = objectMapper.readValue(generatedRequestJson, Map.class);
    assertEquals(expectedRequestJsonMap, generatedRequestJsonMap);
  }

  private String extractRequestBodyJson(HttpMethod httpMethod) throws IOException {
    RequestEntity requestEntity = ((PostMethod) httpMethod).getRequestEntity();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    requestEntity.writeRequest(bos);
    return new String(bos.toByteArray(), StandardCharsets.UTF_8);
  }

  private Template loadVotoTemplate() throws IOException {
    String jsonTemplate =
        "{\n"
            + "        \"name\":\"Voto\",\n"
            + "        \"configurables\": [\n"
            + "            \"api_key\"\n"
            + "        ],\n"
            + "        \"outgoing\":{\n"
            + "            \"maxSmsSize\":\"160\",\n"
            + "            \"millisecondsBetweenMessages\":\"1\",\n"
            + "            \"exponentialBackOffRetries\":\"true\",\n"
            + "            \"maxRecipient\":\"1\",\n"
            + "            \"hasAuthentication\":\"false\",\n"
            + "            \"request\":{\n"
            + "                \"type\":\"POST\",\n"
            + "                \"urlPath\":\"https://url\",\n"
            + "                \"jsonContentType\":\"true\",\n"
            + "                \"bodyParameters\":{\n"
            + "                    \"status_callback_url\":\"[callback]\",\n"
            + "                    \"subscribers\":\"[subscribers]\"\n"
            + "                }\n"
            + "            },\n"
            + "            \"response\":{\n"
            + "            }\n"
            + "        },\n"
            + "        \"status\":{\n"
            + "        },\n"
            + "        \"incoming\":{\n"
            + "        }\n"
            + "    }";
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(jsonTemplate, Template.class);
  }

  private Template loadTemplate(String templateFile) throws IOException {
    try (InputStream templateStream =
        getClass().getClassLoader().getResourceAsStream(templateFile)) {

      if (templateStream == null) {
        throw new IOException("Cannot read template file: " + templateFile);
      }

      String jsonTemplate = IOUtils.toString(templateStream);

      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(jsonTemplate, Template.class);
    }
  }

  private String loadResponse(String responseFile) throws IOException {
    try (InputStream responseStream =
        getClass().getClassLoader().getResourceAsStream(responseFile)) {
      if (responseStream == null) {
        throw new IOException("Cannot read response file: " + responseFile);
      }
      return IOUtils.toString(responseStream);
    }
  }
}
