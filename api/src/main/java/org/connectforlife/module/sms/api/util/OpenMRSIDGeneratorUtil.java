package org.connectforlife.module.sms.api.util;

import java.util.UUID;

public class OpenMRSIDGeneratorUtil {

  public static String generateOpenMRSID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private OpenMRSIDGeneratorUtil() {
  }
}
