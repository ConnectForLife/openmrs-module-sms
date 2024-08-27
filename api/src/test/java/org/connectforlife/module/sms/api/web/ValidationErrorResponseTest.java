package org.connectforlife.module.sms.api.web;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValidationErrorResponseTest {

  private final String code = "code";
  private final String message = "message";
  private Map<String, String> constraintViolations = new HashMap<>();

  @Test
  public void getConstraintViolationsFunctionalityCheck() {
    constraintViolations.put("Violation", "Violation_Message");
    ValidationErrorResponse validationErrorResponse =
        new ValidationErrorResponse(code, message, constraintViolations);
    assertThat(validationErrorResponse.getConstraintViolations(), is(constraintViolations));
  }
}
