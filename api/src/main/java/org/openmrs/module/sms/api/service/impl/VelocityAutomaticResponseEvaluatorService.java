package org.openmrs.module.sms.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.APIException;
import org.openmrs.module.sms.api.data.AutomaticResponseData;
import org.openmrs.module.sms.api.service.AutomaticResponseEvaluatorService;
import org.openmrs.module.sms.api.util.AutomaticResponseEvaluationMessageContext;
import org.openmrs.module.sms.api.util.VelocityContextFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

import static java.text.MessageFormat.format;

/**
 * The VelocityAutomaticResponseEvaluatorService Class.
 *
 * <p>The implementation of AutomaticResponseEvaluatorService which resolves
 * Config#getAutomaticResponseScript() as Apache Velocity Template which creates the text of the
 * automatic response.
 */
public class VelocityAutomaticResponseEvaluatorService
    implements AutomaticResponseEvaluatorService {

  @Override
  public Optional<AutomaticResponseData> evaluate(
      AutomaticResponseEvaluationMessageContext messageContext) {
    if (messageContext.getConfig().getAutomaticResponseScript() == null) {
      return Optional.empty();
    }

    final String scriptResultRaw = executeScript(messageContext);
    return parseResponse(scriptResultRaw, messageContext);
  }

  private String executeScript(AutomaticResponseEvaluationMessageContext messageContext) {
    final VelocityContext velocityContext = VelocityContextFactory.createDefaultContext();
    velocityContext.put("messageContext", messageContext);

    final StringWriter writer = new StringWriter();

    try {
      final boolean evaluationResult =
          Velocity.evaluate(
              velocityContext,
              writer,
              messageContext.getConfig().getName(),
              messageContext.getConfig().getAutomaticResponseScript());

      if (!evaluationResult) {
        throw new APIException(
            format(
                "Failed to execute Velocity Template for automatic response configured by: {0}, see logs for further "
                    + "information.",
                messageContext.getConfig().getName()));
      }
    } catch (IOException | VelocityException e) {
      throw new APIException(
          format(
              "Failed to execute Velocity Template Template for response configured by: {0}. Cause: {1}",
              messageContext.getConfig().getName(), e.toString()),
          e);
    }

    return writer.toString();
  }

  private Optional<AutomaticResponseData> parseResponse(
      String scriptResultRaw, AutomaticResponseEvaluationMessageContext messageContext) {
    if (StringUtils.isBlank(scriptResultRaw)) {
      return Optional.empty();
    }

    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      final AutomaticResponseData response =
          objectMapper.readValue(scriptResultRaw, AutomaticResponseData.class);
      return Optional.of(response);
    } catch (IOException ioe) {
      throw new APIException(
          format(
              "Failed to read result of Velocity Template for automatic response configured by: {0}. Cause: {1}",
              messageContext.getConfig().getName(), ioe.toString()),
          ioe);
    }
  }
}
