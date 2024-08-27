package org.connectforlife.module.sms.web.controller.it;

import org.junit.Test;
import org.connectforlife.module.sms.web.controller.SmsController;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@WebAppConfiguration
public class SmsControllerITTest extends BaseModuleWebContextSensitiveTest {

  private SmsController smsController;

  @Test
  public void onGet() {
    SmsController smsController = new SmsController();
    assertThat(smsController.onGet(), is(notNullValue()));
  }
}
