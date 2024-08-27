package org.connectforlife.module.sms.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class OpenMRSIDGeneratorUtilTest {

  @Test
  public void shouldGenerateOpenMRSID() {
    String actual = OpenMRSIDGeneratorUtil.generateOpenMRSID();

    assertNotNull(actual);
    assertEquals(32, actual.length());
    assertFalse(actual.contains("-"));
  }
}
