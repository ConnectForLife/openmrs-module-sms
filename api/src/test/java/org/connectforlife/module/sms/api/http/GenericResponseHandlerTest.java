package org.connectforlife.module.sms.api.http;

import org.apache.commons.httpclient.Header;
import org.codehaus.jackson.map.ObjectMapper;
import org.connectforlife.module.sms.builder.ConfigBuilder;
import org.connectforlife.module.sms.builder.OutgoingSmsBuilder;
import org.junit.Test;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.templates.Template;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class GenericResponseHandlerTest {

  private final String response = "ID: 0A0000000123ABED1 To: 447700900000";

  @Test
  public void WithSuccessResponseGenericResponseHandler() throws IOException {
    String message = UUID.randomUUID().toString();
    OutgoingSms sms = new OutgoingSmsBuilder().withMessage(message).build();
    Template template = loadTemplateWithSuccessResponse();
    Config config = new ConfigBuilder().buildAsNew();
    GenericResponseHandler genericResponseHandler = new GenericResponseHandler(template, config);
    genericResponseHandler.handle(sms, response, new Header[] {});
    assertThat(genericResponseHandler.getAuditRecords().get(0).getConfig(), is(sms.getConfig()));
    assertThat(
        genericResponseHandler.getAuditRecords().get(0).getMessageContent(), is(sms.getMessage()));
  }

  @Test
  public void WithoutSuccessResponseGenericResponseHandler() throws IOException {
    String message = UUID.randomUUID().toString();
    OutgoingSms sms = new OutgoingSmsBuilder().withMessage(message).build();
    Template template = loadTemplateWithoutSuccessResponse();
    Config config = new ConfigBuilder().buildAsNew();
    GenericResponseHandler genericResponseHandler = new GenericResponseHandler(template, config);
    genericResponseHandler.handle(sms, response, new Header[] {});
    assertThat(genericResponseHandler.getAuditRecords().get(0).getConfig(), is(sms.getConfig()));
    assertThat(
        genericResponseHandler.getAuditRecords().get(0).getMessageContent(), is(sms.getMessage()));
  }

  private Template loadTemplateWithSuccessResponse() throws IOException {
    String jsonTemplate =
        "{\n"
            + "    \"name\": \"nexmo-generic\",\n"
            + "    \"configurables\": [\n"
            + "    \"from\",\n"
            + "    \"api_key\",\n"
            + "    \"api_secret\"\n"
            + "    ],\n"
            + "    \"outgoing\": {\n"
            + "    \"maxSmsSize\": \"160\",\n"
            + "    \"maxRecipient\": \"3\",\n"
            + "    \"millisecondsBetweenMessages\": \"1\",\n"
            + "    \"exponentialBackOffRetries\": \"true\",\n"
            + "    \"hasAuthentication\": \"false\",\n"
            + "    \"request\": {\n"
            + "    \"type\": \"POST\",\n"
            + "    \"urlPath\": \"https://rest.nexmo.com/sms/json\",\n"
            + "    \"recipientsSeparator\": \",\",\n"
            + "    \"bodyParameters\": {\n"
            + "    \"api_key\": \"[api_key]\",\n"
            + "    \"api_secret\": \"[api_secret]\",\n"
            + "    \"from\": \"[from]\",\n"
            + "    \"to\": \"[recipients]\",\n"
            + "    \"text\": \"[message]\"\n"
            + "    }\n"
            + "    },\n"
            + "    \"response\": {\n"
            + "    \"successResponse\": \"true\",\n"
            + "    \"multiLineRecipientResponse\": \"true\",\n"
            + "    \"singleRecipientResponse\": \"true\",\n"
            + "    \"extractSingleSuccessMessageId\": \"\\\"message-id\\\": \\\"([0-9a-zA-Z]+)\\\"\",\n"
            + "    \"extractSingleFailureMessage\": \"^ERR: (.*)$\",\n"
            + "    \"extractSuccessMessageIdAndRecipient\": \"^ID: ([0-9a-zA-Z]+) To: ([0-9]+)$\",\n"
            + "    \"extractFailureMessageAndRecipient\": \"^ERR: (.*) To: ([0-9]+)$\",\n"
            + "    \"extractProviderStatus\": \"\\\"status\\\": \\\"([0-9a-zA-Z]+)\\\"\"\n"
            + "    }\n"
            + "    },\n"
            + "    \"status\": {\n"
            + "    \"messageIdKey\": \"apiMsgId\",\n"
            + "    \"statusKey\": \"status\",\n"
            + "    \"statusSuccess\": \"003|004|008\",\n"
            + "    \"statusFailure\": \"005|006|007|009|010|012|014\"\n"
            + "    },\n"
            + "    \"incoming\": {\n"
            + "    \"messageKey\": \"text\",\n"
            + "    \"senderKey\": \"from\",\n"
            + "    \"recipientKey\": \"to\",\n"
            + "    \"msgIdKey\": \"moMsgId\",\n"
            + "    \"timestampKey\": \"timestamp\"\n"
            + "    }\n"
            + "    }\n"
            + "  }";
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(jsonTemplate, Template.class);
  }

  private Template loadTemplateWithoutSuccessResponse() throws IOException {
    String jsonTemplate =
        "{\n"
            + "    \"name\": \"nexmo-generic\",\n"
            + "    \"configurables\": [\n"
            + "    \"from\",\n"
            + "    \"api_key\",\n"
            + "    \"api_secret\"\n"
            + "    ],\n"
            + "    \"outgoing\": {\n"
            + "    \"maxSmsSize\": \"160\",\n"
            + "    \"maxRecipient\": \"3\",\n"
            + "    \"millisecondsBetweenMessages\": \"1\",\n"
            + "    \"exponentialBackOffRetries\": \"true\",\n"
            + "    \"hasAuthentication\": \"false\",\n"
            + "    \"request\": {\n"
            + "    \"type\": \"POST\",\n"
            + "    \"urlPath\": \"https://rest.nexmo.com/sms/json\",\n"
            + "    \"recipientsSeparator\": \",\",\n"
            + "    \"bodyParameters\": {\n"
            + "    \"api_key\": \"[api_key]\",\n"
            + "    \"api_secret\": \"[api_secret]\",\n"
            + "    \"from\": \"[from]\",\n"
            + "    \"to\": \"[recipients]\",\n"
            + "    \"text\": \"[message]\"\n"
            + "    }\n"
            + "    },\n"
            + "    \"response\": {\n"
            + "    \"multiLineRecipientResponse\": \"true\",\n"
            + "    \"singleRecipientResponse\": \"true\",\n"
            + "    \"extractSingleSuccessMessageId\": \"\\\"message-id\\\": \\\"([0-9a-zA-Z]+)\\\"\",\n"
            + "    \"extractSingleFailureMessage\": \"^ERR: (.*)$\",\n"
            + "    \"extractSuccessMessageIdAndRecipient\": \"^ID: ([0-9a-zA-Z]+) To: ([0-9]+)$\",\n"
            + "    \"extractFailureMessageAndRecipient\": \"^ERR: (.*) To: ([0-9]+)$\",\n"
            + "    \"extractProviderStatus\": \"\\\"status\\\": \\\"([0-9a-zA-Z]+)\\\"\"\n"
            + "    }\n"
            + "    },\n"
            + "    \"status\": {\n"
            + "    \"messageIdKey\": \"apiMsgId\",\n"
            + "    \"statusKey\": \"status\",\n"
            + "    \"statusSuccess\": \"003|004|008\",\n"
            + "    \"statusFailure\": \"005|006|007|009|010|012|014\"\n"
            + "    },\n"
            + "    \"incoming\": {\n"
            + "    \"messageKey\": \"text\",\n"
            + "    \"senderKey\": \"from\",\n"
            + "    \"recipientKey\": \"to\",\n"
            + "    \"msgIdKey\": \"moMsgId\",\n"
            + "    \"timestampKey\": \"timestamp\"\n"
            + "    }\n"
            + "    }\n"
            + "  }";
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(jsonTemplate, Template.class);
  }
}
