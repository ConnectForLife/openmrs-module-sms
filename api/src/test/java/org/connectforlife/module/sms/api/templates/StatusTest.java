package org.connectforlife.module.sms.api.templates;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class StatusTest {

  private static final String MessageIdKey = "MESSAGE_ID_KEY";
  private static final String StatusIdKey = "STATUS_ID_KEY";
  private static final String StatusSuccess = "success";
  private static final String StatusFailure = "Failed";

  @Test
  public void statusWithStatusSuccess() {
    Status status = new Status();
    status.setStatusKey(StatusIdKey);
    status.setMessageIdKey(MessageIdKey);
    status.setStatusSuccess(StatusSuccess);
    assertTrue(status.hasStatusKey());
    assertTrue(status.hasMessageIdKey());
    assertTrue(status.hasStatusSuccess());
    assertFalse(status.hasStatusFailure());
    assertThat(status.getStatusKey(), is(StatusIdKey));
    assertThat(status.getMessageIdKey(), is(MessageIdKey));
    assertThat(status.getStatusSuccess(), is(StatusSuccess));
  }

  @Test
  public void statusWithStatusFailure() {
    Status status = new Status();
    status.setStatusKey(StatusIdKey);
    status.setMessageIdKey(MessageIdKey);
    status.setStatusFailure(StatusFailure);
    assertTrue(status.hasStatusKey());
    assertTrue(status.hasMessageIdKey());
    assertTrue(status.hasStatusFailure());
    assertFalse(status.hasStatusSuccess());
    assertThat(status.getStatusKey(), is(StatusIdKey));
    assertThat(status.getMessageIdKey(), is(MessageIdKey));
    assertThat(status.getStatusFailure(), is(StatusFailure));
  }
}
