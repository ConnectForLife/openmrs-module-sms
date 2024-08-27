/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.util;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TemplateOptionalPropertyUtil Class.
 *
 * <p>Provides utilities to handle optional parameters in configuration templates.
 */
public class TemplateOptionalParameterUtil {
  private static final Pattern FIND_SPEL_EXPRESSION_PATTERN =
      Pattern.compile("^(\\w*)\\[#\\{(!?.*)}]$");
  private static final int PROPERTY_NAME = 1;
  private static final int EXPRESSION = 2;
  private static final ExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

  private TemplateOptionalParameterUtil() {}

  /**
   * Checks if {@code parameterName} contains optional Spring EL expression to use to evaluate if
   * the parameter should be included or not.
   *
   * <p>The parameter with optional expression follows pattern: {@code
   * <parameterName>[<spEl-expression>]}.
   *
   * <p>The method checks if optional expression exists, then returns the result from the
   * expression.
   *
   * @return true if the {@code parameterName} doesn't contain optional expression or the value
   *     resolved from expression
   */
  public static boolean shouldParameterBeIncluded(
      String parameterName, Map<String, Object> contextProperties) {
    final Matcher spELExpressionMatcher = FIND_SPEL_EXPRESSION_PATTERN.matcher(parameterName);

    final boolean result;
    if (spELExpressionMatcher.matches()) {
      result = resolveSpELExpression(contextProperties, spELExpressionMatcher.group(EXPRESSION));
    } else {
      result = true;
    }

    return result;
  }

  public static String trimOptionalExpression(String parameterName) {
    final Matcher spELExpressionMatcher = FIND_SPEL_EXPRESSION_PATTERN.matcher(parameterName);
    if (spELExpressionMatcher.matches()) {
      return spELExpressionMatcher.group(PROPERTY_NAME);
    }
    return parameterName;
  }

  private static boolean resolveSpELExpression(
      Map<String, Object> contextProperties, String spElExpression) {
    final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    evaluationContext.setVariables(contextProperties);
    return Boolean.TRUE.equals(
        SPEL_EXPRESSION_PARSER.parseExpression(spElExpression)
            .getValue(evaluationContext, Boolean.class));
  }
}
