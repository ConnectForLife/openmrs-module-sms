package org.connectforlife.module.sms.api.templates;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TemplatesTest extends BaseModuleContextSensitiveTest {

  private Template actual_template;

  private String VALID_TEMPLATE = "Voto";
  private String INVALID_TEMPLATE = "Invalid_Template";

  @Before
  public void setUp() throws IOException {
    actual_template = loadVotoTemplate();
  }

  @Test
  public void getTemplateFunctionalityCheck() {
    Templates templates = new Templates(Collections.singletonList(actual_template));
    Template expected_template = templates.getTemplate(VALID_TEMPLATE);
    Map<String, TemplateForWeb> expected_templatesForWeb = templates.templatesForWeb();
    assertThat(
        expected_template.getStatus().getMessageIdKey(),
        is(actual_template.getStatus().getMessageIdKey()));
    assertNull(templates.getTemplate(INVALID_TEMPLATE));
    assertNotNull(expected_templatesForWeb);
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
}
