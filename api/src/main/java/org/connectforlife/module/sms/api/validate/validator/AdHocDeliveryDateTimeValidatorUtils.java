package org.connectforlife.module.sms.api.validate.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdHocDeliveryDateTimeValidatorUtils {

  private static final String AD_HOC_SMS_TIME_REGEX = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

  public static boolean isValidTime(String time) {
    if (time == null) {
      return false;
    }

    Pattern pattern = Pattern.compile(AD_HOC_SMS_TIME_REGEX);
    Matcher matcher = pattern.matcher(time);

    return matcher.matches();
  }

  private AdHocDeliveryDateTimeValidatorUtils() {
  }
}
