package org.connectforlife.module.sms.api.data;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AutomaticResponseDataTest {

  private static final String MESSAGE = "Test_Message";
  private static final String CONFIG = "config";
  private static final String OPENMRS_ID = "openmrs_id";
  private static final String PROVIDER_MESSAGE_ID = "provider_message_id";
  private static final List<String> RECIPIENTS_LIST = Arrays.asList("recipient1", "recipient2");

  private AutomaticResponseData automaticResponseData = new AutomaticResponseData();

  @Test
  public void setMessage() {
    automaticResponseData.setMessage(MESSAGE);
    assertThat(automaticResponseData.getMessage(), is(MESSAGE));
  }

  @Test
  public void setCustomParameters() {
    Map<String, Object> CUSTOM_PARAM = new HashMap<>();
    CUSTOM_PARAM.put("CONFIG", CONFIG);
    CUSTOM_PARAM.put("RECIPIENTS", RECIPIENTS_LIST);
    CUSTOM_PARAM.put("OPENMRS_ID", OPENMRS_ID);
    CUSTOM_PARAM.put("PROVIDER_MESSAGE_ID", PROVIDER_MESSAGE_ID);

    automaticResponseData.setCustomParameters(CUSTOM_PARAM);
    assertThat(automaticResponseData.getCustomParameters(), is(CUSTOM_PARAM));
  }
}
