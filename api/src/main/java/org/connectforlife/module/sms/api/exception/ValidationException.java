/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.exception;

import com.google.gson.GsonBuilder;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class ValidationException extends RuntimeException {

  private static final long serialVersionUID = -7949334830813519654L;

  /** Constraint violations that describe the error causes. */
  private final Map<String, String> constraintViolations;

  /**
   * Creates new exception according to constraint violations.
   *
   * @param constraintViolations set of constraints that were violated
   */
  public ValidationException(Map<String, String> constraintViolations) {
    this.constraintViolations = constraintViolations;
  }

  @Override
  public String toString() {
    return MessageFormat.format("{0}\n{1}", super.toString(), toJson(constraintViolations));
  }

  private static String toJson(Map<String, String> violations) {
    return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(violations);
  }

  public Map<String, String> getConstraintViolations() {
    return Collections.unmodifiableMap(constraintViolations);
  }
}
