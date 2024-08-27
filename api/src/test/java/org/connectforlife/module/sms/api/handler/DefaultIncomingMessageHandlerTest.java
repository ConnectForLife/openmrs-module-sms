package org.connectforlife.module.sms.api.handler;

import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.handler.impl.DefaultIncomingMessageHandler;
import org.connectforlife.module.sms.api.service.AutomaticResponseEvaluatorService;
import org.connectforlife.module.sms.api.service.impl.VelocityAutomaticResponseEvaluatorService;
import org.connectforlife.module.sms.api.templates.Incoming;
import org.connectforlife.module.sms.api.templates.Status;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.util.IncomingMessageAccessor;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class DefaultIncomingMessageHandlerTest extends BaseModuleContextSensitiveTest {

  DefaultIncomingMessageHandler defaultIncomingMessageHandler;

  IncomingMessageAccessor incomingMessageAccessor;

  private static final int expected_priority = 10;
  private final String message_id = UUID.randomUUID().toString();
  private final String message_key = "message";
  private final String sender_key = "sender";
  private final String status_key = "status";

  @Before
  public void setUp() {
    defaultIncomingMessageHandler = new DefaultIncomingMessageHandler();
    AutomaticResponseEvaluatorService automaticResponseEvaluatorService =
        new VelocityAutomaticResponseEvaluatorService();
    defaultIncomingMessageHandler.setAutomaticResponseEvaluatorService(
        automaticResponseEvaluatorService);
    Incoming incoming = new Incoming();
    incoming.setMsgIdKey(message_id);
    incoming.setMessageKey(message_key);
    incoming.setSenderKey(sender_key);
    incoming.setSenderRegex("(.*)");
    incoming.setMessageRegex("(.*)");
    Status status = new Status();
    status.setStatusKey(status_key);
    Template template = new Template();
    template.setIncoming(incoming);
    template.setStatus(status);
    Map<String, String> providerData = new HashMap<>();
    providerData.put(message_key, "Message");
    providerData.put(message_id, "897");
    providerData.put(sender_key, "1234345");
    providerData.put(status_key, "Delivered");
    incomingMessageAccessor = new IncomingMessageAccessor(template, providerData);
  }

  @Test
  public void priority() {
    int actual = defaultIncomingMessageHandler.priority();
    assertThat(actual, is(expected_priority));
  }

  @Test
  public void shouldReturnFalseForMessageNotReceivedFirstTime() {
    IncomingMessageData incomingMessageData =
        new IncomingMessageDataBuilder(incomingMessageAccessor)
            .setReceivedForAFistTime(Boolean.FALSE)
            .build();
    assertFalse(defaultIncomingMessageHandler.handle(incomingMessageData));
  }

  @Test
  public void shouldReturnFalseMessageReceivedFirstTimeWithoutAutomaticResponseScript() {
    Config config = new Config();
    config.setName("Nexmo");
    IncomingMessageData incomingMessageData =
        new IncomingMessageDataBuilder(incomingMessageAccessor)
            .setConfig(config)
            .setReceivedForAFistTime(Boolean.TRUE)
            .build();
    assertFalse(defaultIncomingMessageHandler.handle(incomingMessageData));
  }
}
