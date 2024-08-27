package org.connectforlife.module.sms.api.service;

import org.connectforlife.module.sms.builder.ConfigBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.connectforlife.module.sms.TestConstants;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.handler.IncomingMessageData;
import org.connectforlife.module.sms.api.handler.IncomingMessageDataBuilder;
import org.connectforlife.module.sms.api.service.impl.IncomingMessageServiceImpl;
import org.connectforlife.module.sms.api.util.SMSConstants;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class IncomingMessageServiceImplTest {

  @Mock private AdministrationService administrationService;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    given(Context.getAdministrationService()).willReturn(administrationService);
    given(
            administrationService.getGlobalProperty(
                SMSConstants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS))
        .willReturn(String.valueOf(Boolean.FALSE));
  }

  @Test
  public void testHandleMessageSafe() throws Exception {
    IncomingMessageService incomingMessageService = new IncomingMessageServiceImpl();
    Config config = new ConfigBuilder().build();
    IncomingMessageData incomingMessageData =
        new IncomingMessageDataBuilder().setConfig(config).build();
    DaemonToken daemonToken = getDaemonToken();
    incomingMessageService.setDaemonToken(daemonToken);
    incomingMessageService.handleMessageSafe(incomingMessageData);
    verify(administrationService)
        .getGlobalProperty(SMSConstants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS);
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
