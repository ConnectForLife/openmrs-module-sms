package org.connectforlife.module.sms.api.validate.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AdHocDeliveryDateTimeValidatorUtilsTest {

  @Test
  public void shouldValidationPassWhenAdHocTimeRegexHasValidFormat() {
    boolean actual = AdHocDeliveryDateTimeValidatorUtils.isValidTime("13:25");

    assertTrue(actual);
  }

  @Test
  public void shouldValidationPassWhenAdHocTimeRegexHasInvalidFormat() {
    boolean actual = AdHocDeliveryDateTimeValidatorUtils.isValidTime("38:87");

    assertFalse(actual);
  }
}
