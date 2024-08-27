package org.connectforlife.module.sms.api.event;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.connectforlife.module.sms.TestConstants;
import org.connectforlife.module.sms.api.http.SmsHttpService;
import org.connectforlife.module.sms.api.util.SmsEventParamsConstants;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class StatusSmsEventListenerTest {

  @Mock StatusSmsEventListener statusSmsEventListener;

  private Session session;

  private SmsHttpService smsHttpService;

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

  @Before
  public void setUp() throws Exception {
    mockStatic(Context.class);
    statusSmsEventListener = new StatusSmsEventListener();
    session = mock(Session.class);
    ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();
    activeMQMapMessage.setString("config", "nexmo");
    activeMQMapMessage.setString("message", "messages");
    when(session.createMessage()).thenReturn(activeMQMapMessage);
    smsHttpService = mock(SmsHttpService.class);
    doReturn(smsHttpService)
        .when(Context.class, "getRegisteredComponent", "sms.SmsHttpService", SmsHttpService.class);
  }

  @Test
  public void getSubjects() {
    String[] actual =
        new String[] {
          SmsEventSubjectsConstants.PENDING,
          SmsEventSubjectsConstants.SCHEDULED,
          SmsEventSubjectsConstants.RETRYING
        };
    String[] expected = statusSmsEventListener.getSubjects();
    assertThat(expected, is(actual));
  }

  @Test
  public void onMessage()
      throws JMSException, InvocationTargetException, NoSuchMethodException,
          IllegalAccessException {
    Message message = session.createMessage();
    message.setObjectProperty(SmsEventParamsConstants.CONFIG, CONFIG);
    message.setObjectProperty(SmsEventParamsConstants.RECIPIENTS, RECIPIENTS_LIST);
    message.setObjectProperty(SmsEventParamsConstants.MESSAGE, MESSAGE);
    message.setObjectProperty(SmsEventParamsConstants.OPENMRS_ID, OPENMRS_ID);
    message.setObjectProperty(SmsEventParamsConstants.PROVIDER_MESSAGE_ID, PROVIDER_MESSAGE_ID);
    message.setObjectProperty(SmsEventParamsConstants.FAILURE_COUNT, FAILURE_COUNT);
    message.setObjectProperty(SmsEventParamsConstants.CUSTOM_PARAMS, CUSTOM_PARAMS);
    statusSmsEventListener.setDaemonToken(getDaemonToken());
    statusSmsEventListener.onMessage(message);
    verify(smsHttpService).send(anyObject());
  }

  private DaemonToken getDaemonToken()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Module module = new Module(TestConstants.MODULE_ID);
    module.setModuleId(TestConstants.MODULE_ID);
    Method method = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
    method.setAccessible(true);
    return (DaemonToken) method.invoke(null, module);
  }
}
