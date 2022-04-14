package org.openmrs.module.sms.api.util;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.regex.Matcher.quoteReplacement;

/**
 * The TemplatePlaceholderReplaceUtil Class.
 *
 * <p>Provides utilities to replace tokens in SMS template Json strings.
 */
public class TemplatePlaceholderReplaceUtil {
  private static final Pattern FIND_STRING_TOKEN_PATTERN = Pattern.compile("\\[(\\w+)]");
  private static final Pattern FIND_ARRAY_TOKEN_PATTERN = Pattern.compile("\\[\\((.*)\\)]");

  private static final String ARRAY_START = "[";
  private static final String ARRAY_END = "]";
  private static final String ARRAY_ITEM_DELIMITER = ",";

  /**
   * Replace placeholders in give {@code inputText} with values from {@code props} Map.
   *
   * <p>Placeholders like {@code Hello [name]} are replaced with a value with a key `name` from
   * {@code props} map. E.g.: for `name=Jan`, the result is {@code Hello Jan}
   *
   * <p>Placeholders like {@code [(hello [names])]} are replaced by resolving the inner section into
   * an array. E.g.: for `name=[Jan, Joe], the result is {@code [hello Jan, hello Joe]}
   *
   * @param inputText input string
   * @param props values to use in replacement
   * @return input string replacing [tokens] with their values from props
   */
  public static String placeholderOrLiteral(String inputText, Map<String, Object> props) {
    final String replacedArrayTokens = replaceArrayToken(inputText, props);
    return replaceStringTokens(replacedArrayTokens, props, true);
  }

  private static String replaceArrayToken(String inputText, Map<String, Object> props) {
    final StringBuffer result = new StringBuffer();
    final Matcher inputTextMatcher = FIND_ARRAY_TOKEN_PATTERN.matcher(inputText);

    while (inputTextMatcher.find()) {
      final String arrayItemBody = replaceStringTokens(inputTextMatcher.group(1), props, false);
      final String arrayText = resolveArray(arrayItemBody, props);
      inputTextMatcher.appendReplacement(result, quoteReplacement(arrayText));
    }
    inputTextMatcher.appendTail(result);

    return result.toString();
  }

  private static String resolveArray(String arrayItemBody, Map<String, Object> props) {
    final StringJoiner arrayTextJoiner =
        new StringJoiner(ARRAY_ITEM_DELIMITER, ARRAY_START, ARRAY_END);
    final Matcher arrayItemBodyMatcher = FIND_STRING_TOKEN_PATTERN.matcher(arrayItemBody);

    if (arrayItemBodyMatcher.find()) {
      final String arrayPropertyName = arrayItemBodyMatcher.group(1);
      final Iterable<String> arrayValue = getSafeIterable(props.get(arrayPropertyName));

      for (String arrayValueItem : arrayValue) {
        arrayTextJoiner.add(
            arrayItemBodyMatcher.replaceAll(quoteReplacement(removeNewLines(arrayValueItem))));
      }
    }

    return arrayTextJoiner.toString();
  }

  private static String removeNewLines(String text) {
    return text.replaceAll("\r?\n", "");
  }

  private static String replaceStringTokens(
      String inputText, Map<String, Object> props, boolean alwaysReplace) {
    final StringBuffer result = new StringBuffer();
    final Matcher replaceTokenMatcher = FIND_STRING_TOKEN_PATTERN.matcher(inputText);

    while (replaceTokenMatcher.find()) {
      final String propertyName = replaceTokenMatcher.group(1);
      final Object propertyValue = props.get(propertyName);

      if (propertyValue instanceof String) {
        replaceTokenMatcher.appendReplacement(result, quoteReplacement(propertyValue.toString()));
      } else if (propertyValue != null) {
        final String replacement =
            alwaysReplace ? propertyValue.toString() : wrapAsStringToken(propertyName);
        replaceTokenMatcher.appendReplacement(result, quoteReplacement(replacement));
      } else {
        throw new IllegalStateException(
            MessageFormat.format("Template error! Unable to find value for ''{0}''", propertyName));
      }
    }
    replaceTokenMatcher.appendTail(result);

    return result.toString();
  }

  private static Iterable<String> getSafeIterable(Object value) {
    if (value == null) {
      return Collections.emptyList();
    } else if (value instanceof Iterable) {
      return StreamSupport.stream(((Iterable<?>) value).spliterator(), false)
          .map(Object::toString)
          .collect(Collectors.toList());
    } else {
      return Collections.singletonList(value.toString());
    }
  }

  private static String wrapAsStringToken(String text) {
    return "[" + text + "]";
  }

  private TemplatePlaceholderReplaceUtil() {}
}
