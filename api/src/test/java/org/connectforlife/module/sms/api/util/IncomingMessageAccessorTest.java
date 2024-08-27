package org.connectforlife.module.sms.api.util;

import org.junit.Test;
import org.connectforlife.module.sms.api.templates.Incoming;
import org.connectforlife.module.sms.api.templates.Status;
import org.connectforlife.module.sms.api.templates.Template;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IncomingMessageAccessorTest {

  private final String message_id = UUID.randomUUID().toString();
  private final String message_key = "message";
  private final String sender_key = "sender";
  private final String status_key = "status";

  @Test
  public void checkFunctionality() {
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
    IncomingMessageAccessor incomingMessageAccessor =
        new IncomingMessageAccessor(template, providerData);
    assertThat(incomingMessageAccessor.getMessage(), is(providerData.get(message_key)));
    assertThat(incomingMessageAccessor.getStatus(), is(providerData.get(status_key)));
    assertThat(incomingMessageAccessor.getMsgId(), is(providerData.get(message_id)));
    assertThat(incomingMessageAccessor.getSender(), is(providerData.get(sender_key)));
  }
}
