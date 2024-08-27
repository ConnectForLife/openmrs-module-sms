package org.connectforlife.module.sms.api.web;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ErrorResponseTest {

  private final String Code = "Code";
  private final String Message = "Message";

  @Test
  public void checkFunctionality() {
    ErrorResponse errorResponse = new ErrorResponse(null, null);
    errorResponse.setCode(Code);
    errorResponse.setMessage(Message);
    assertThat(errorResponse.getCode(), is(Code));
    assertThat(errorResponse.getMessage(), is(Message));
  }
}
