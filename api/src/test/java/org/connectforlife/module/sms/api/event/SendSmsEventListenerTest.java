package org.connectforlife.module.sms.api.event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.connectforlife.module.sms.api.service.SmsService;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.SmsEventParamsConstants;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class SendSmsEventListenerTest {

  private static final String CONFIG = "config";
  private static final String MESSAGE = "message";
  private static final String OPENMRS_ID = "openmrs_id";
  private static final String PROVIDER_MESSAGE_ID = "provider_message_id";
  private static final Integer FAILURE_COUNT = 2;
  private static final List<String> RECIPIENTS_LIST = Arrays.asList("recipient1", "recipient2");
  private static final Map<String, String> CUSTOM_PARAMS =
      new HashMap<String, String>() {
        {
          put("key1", "value1");
          put("key2", "value2");
        }
      };
  private static final Date DELIVERY_TIME = DateUtil.parse("2012-01-10T00:00:00.000+06:00");
  private Map<String, Object> params;

  private SendSmsEventListener sendSmsEventListener;

  private SmsService smsService;

  @Before
  public void setUp() throws Exception {
    mockStatic(Context.class);
    smsService = mock(SmsService.class);
    when(Context.getRegisteredComponent("sms.SmsService", SmsService.class)).thenReturn(smsService);

    params = new HashMap<>();
    params.put(SmsEventParamsConstants.CONFIG, CONFIG);
    params.put(SmsEventParamsConstants.RECIPIENTS, RECIPIENTS_LIST);
    params.put(SmsEventParamsConstants.MESSAGE, MESSAGE);
    params.put(SmsEventParamsConstants.OPENMRS_ID, OPENMRS_ID);
    params.put(SmsEventParamsConstants.PROVIDER_MESSAGE_ID, PROVIDER_MESSAGE_ID);
    params.put(SmsEventParamsConstants.FAILURE_COUNT, FAILURE_COUNT);
    params.put(SmsEventParamsConstants.DELIVERY_TIME, DELIVERY_TIME);
    params.put(SmsEventParamsConstants.CUSTOM_PARAMS, CUSTOM_PARAMS);
    sendSmsEventListener = new SendSmsEventListener();
  }

  @Test
  public void getSubjects() {
    String[] expected = sendSmsEventListener.getSubjects();
    String[] actual = new String[] {SmsEventSubjectsConstants.SEND_SMS};
    assertThat(expected, is(actual));
  }

  @Test
  public void handleEvent() {
    sendSmsEventListener.handleEvent(params);
    verify(smsService).send(anyObject());
  }
}
