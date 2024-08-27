package org.connectforlife.module.sms.api.service;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.connectforlife.module.sms.api.data.AdHocSMSData;
import org.connectforlife.module.sms.api.event.SmsEvent;
import org.connectforlife.module.sms.api.service.impl.ScheduleAdHocSMSesServiceImpl;
import org.connectforlife.module.sms.api.task.AbstractSmsTask;
import org.connectforlife.module.sms.api.util.SMSConstants;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class ScheduleAdHocSMSesServiceTest {

  private final ScheduleAdHocSMSesService scheduleAdHocSMSesService = new ScheduleAdHocSMSesServiceImpl();

  @Mock
  private SmsSchedulerService smsSchedulerService;

  @Mock
  private AdministrationService administrationService;

  @Before
  public void setUp() {
    mockStatic(Context.class);

    when(Context.getService(SmsSchedulerService.class)).thenReturn(smsSchedulerService);
    when(Context.getAdministrationService()).thenReturn(administrationService);
  }

  @Test
  public void shouldScheduleAdHocSMSes() {
    when(administrationService.getGlobalProperty(SMSConstants.DEFAULT_USER_TIMEZONE)).thenReturn(
        null);
    scheduleAdHocSMSesService.scheduleAdHocSMSes(buildTestAdHocSMSDataList());

    verify(smsSchedulerService, times(3)).safeScheduleRunOnceJob(any(SmsEvent.class),
        any(Date.class), any(AbstractSmsTask.class));
  }

  private List<AdHocSMSData> buildTestAdHocSMSDataList() {
    return Arrays.asList(
        buildTestAdHocSMSDataObject("111222333", "text1", new HashMap<>(), "2022-08-27 13:40", "config1"),
        buildTestAdHocSMSDataObject("555666777", "text2", new HashMap<>(), "2022-08-25 16:25",
            "config2"),
        buildTestAdHocSMSDataObject("22446688", "text3", new HashMap<>(), "2022-11-08 08:45", "config3"));
  }

  private AdHocSMSData buildTestAdHocSMSDataObject(String phone, String smsText,
      Map<String, Object> params,
      String contactTime, String config) {
    AdHocSMSData adHocSMSData = new AdHocSMSData();
    adHocSMSData.setPhone(phone);
    adHocSMSData.setSmsText(smsText);
    adHocSMSData.setParameters(params);
    adHocSMSData.setContactTime(contactTime);
    adHocSMSData.setConfig(config);

    return adHocSMSData;
  }
}

