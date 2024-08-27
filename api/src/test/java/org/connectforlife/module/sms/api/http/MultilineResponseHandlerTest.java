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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class MultilineResponseHandlerTest {

  private final String PROVIDER_ID_1 = "0A0000000123ABED1";
  private final String PROVIDER_ID_2 = "0A00120000123A12CD1";
  private final String message = UUID.randomUUID().toString();
  private static final List<String> recipients = Arrays.asList("1224578", "90123456");

  @Test
  public void withProperSuccessResponseMultilineResponseHandler() throws IOException {
    OutgoingSms sms =
        new OutgoingSmsBuilder().withRecipients(recipients).withMessage(message).build();
    String response =
        "ID: "
            + PROVIDER_ID_1
            + " To: "
            + recipients.get(0)
            + "\nID: "
            + PROVIDER_ID_2
            + " To: "
            + recipients.get(1);
    Template template = loadTemplate();
    Config config = new ConfigBuilder().buildAsNew();
    MultilineResponseHandler multilineResponseHandler =
        new MultilineResponseHandler(template, config);
    multilineResponseHandler.handle(sms, response, new Header[] {});
    assertThat(multilineResponseHandler.getAuditRecords().get(0).getConfig(), is(sms.getConfig()));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(0).getMessageContent(),
        is(sms.getMessage()));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(0).getPhoneNumber(), is(recipients.get(0)));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(1).getPhoneNumber(), is(recipients.get(1)));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(0).getProviderId(), is(PROVIDER_ID_1));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(1).getProviderId(), is(PROVIDER_ID_2));
  }

  @Test
  public void withProperFailureResponseMultilineResponseHandler() throws IOException {
    OutgoingSms sms =
        new OutgoingSmsBuilder().withRecipients(recipients).withMessage(message).build();
    String response =
        "ERR: "
            + "ERR_1"
            + " To: "
            + recipients.get(0)
            + "\nERR: "
            + "ERR_2"
            + " To: "
            + recipients.get(1);
    Template template = loadTemplate();
    Config config = new ConfigBuilder().buildAsNew();
    MultilineResponseHandler multilineResponseHandler =
        new MultilineResponseHandler(template, config);
    multilineResponseHandler.handle(sms, response, new Header[] {});
    assertThat(multilineResponseHandler.getAuditRecords().get(0).getConfig(), is(sms.getConfig()));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(0).getMessageContent(),
        is(sms.getMessage()));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(0).getPhoneNumber(), is(recipients.get(0)));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(1).getPhoneNumber(), is(recipients.get(1)));
  }

  @Test
  public void withoutProperSuccessResponseAndWithoutProperFailureResponseMultilineResponseHandler()
      throws IOException {
    OutgoingSms sms =
        new OutgoingSmsBuilder().withRecipients(recipients).withMessage(message).build();
    String response =
        "Invalid: Invalid_1"
            + " To: "
            + recipients.get(0)
            + "\nInvalid: "
            + "Invalid_2"
            + " To: "
            + recipients.get(1);
    Template template = loadTemplate();
    Config config = new ConfigBuilder().buildAsNew();
    MultilineResponseHandler multilineResponseHandler =
        new MultilineResponseHandler(template, config);
    multilineResponseHandler.handle(sms, response, new Header[] {});
    assertThat(multilineResponseHandler.getAuditRecords().get(0).getConfig(), is(sms.getConfig()));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(0).getMessageContent(),
        is(sms.getMessage()));
    assertThat(
        multilineResponseHandler.getAuditRecords().get(0).getPhoneNumber(),
        is(recipients.toString()));
  }

  private Template loadTemplate() throws IOException {
    String jsonTemplate =
        "{\n"
            + "    \"name\": \"nexmo\",\n"
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
}
