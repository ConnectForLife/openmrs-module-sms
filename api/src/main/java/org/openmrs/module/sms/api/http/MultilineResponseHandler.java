package org.openmrs.module.sms.api.http;

import org.apache.commons.httpclient.Header;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.DateUtil;

import java.util.Collections;
import java.util.List;

import static org.openmrs.module.sms.api.audit.SmsDirection.OUTBOUND;
import static org.openmrs.module.sms.api.util.SmsEventsHelper.outboundEvent;

/** Deals with multi-line responses, like the ones sent by Clickatell. */
public class MultilineResponseHandler extends ResponseHandler {

  /**
   * Constructs an instance using the provided template and configuration.
   *
   * @param template the template to use
   * @param config the configuration to use
   */
  MultilineResponseHandler(Template template, Config config) {
    super(template, config);
  }

  @SuppressWarnings("PMD.ExcessiveMethodLength")
  @Override
  public void handle(OutgoingSms sms, String response, Header[] headers) {

    //
    // as the class name suggest we're dealing with a provider which returns a status code for each
    // individual
    // recipient phone number in the original request on a separate line, ie: if we send an SMS to 4
    // recipients
    // then we should receive four lines of provider_message_id & status information
    //
    for (String responseLine : response.split("\\r?\\n")) {

      String[] messageIdAndRecipient =
          getTemplateOutgoingResponse().extractSuccessMessageIdAndRecipient(responseLine);

      String providerStatus = getTemplateOutgoingResponse().extractProviderStatus(responseLine);

      if (messageIdAndRecipient == null) {
        Integer failureCount = sms.getFailureCount() + 1;
        String[] messageAndRecipient;

        messageAndRecipient =
            getTemplateOutgoingResponse().extractFailureMessageAndRecipient(responseLine);
        if (messageAndRecipient == null) {
          getEvents()
              .add(
                  outboundEvent(
                      getConfig().retryOrAbortSubject(failureCount),
                      getConfig().getName(),
                      sms.getRecipients(),
                      sms.getMessage(),
                      sms.getOpenMrsId(),
                      null,
                      failureCount,
                      providerStatus,
                      null,
                      sms.getCustomParams()));

          String errorMessage =
              String.format(
                  "Failed to send SMS. Template error. Can't parse response: %s", responseLine);
          getLogger().error(errorMessage);

          getAuditRecords()
              .add(
                  new SmsRecord(
                      getConfig().getName(),
                      OUTBOUND,
                      sms.getRecipients().toString(),
                      sms.getMessage(),
                      DateUtil.now(),
                      getConfig().retryOrAbortStatus(failureCount),
                      providerStatus,
                      sms.getOpenMrsId(),
                      null,
                      null));
        } else {
          String failureMessage = messageAndRecipient[0];
          String recipient = messageAndRecipient[1];
          List<String> recipients = Collections.singletonList(recipient);
          getEvents()
              .add(
                  outboundEvent(
                      getConfig().retryOrAbortSubject(failureCount),
                      getConfig().getName(),
                      recipients,
                      sms.getMessage(),
                      sms.getOpenMrsId(),
                      null,
                      failureCount,
                      providerStatus,
                      null,
                      sms.getCustomParams()));
          getLogger().info(String.format("Failed to send SMS: %s", failureMessage));
          getAuditRecords()
              .add(
                  new SmsRecord(
                      getConfig().getName(),
                      OUTBOUND,
                      recipient,
                      sms.getMessage(),
                      DateUtil.now(),
                      getConfig().retryOrAbortStatus(failureCount),
                      providerStatus,
                      sms.getOpenMrsId(),
                      null,
                      failureMessage));
        }
      } else {
        String messageId = messageIdAndRecipient[0];
        String recipient = messageIdAndRecipient[1];

        // todo: HIPAA concerns?
        getLogger()
            .info(
                String.format(
                    "Sent messageId %s '%s' to %s", messageId, messageForLog(sms), recipient));
        getAuditRecords()
            .add(
                new SmsRecord(
                    getConfig().getName(),
                    OUTBOUND,
                    recipient,
                    sms.getMessage(),
                    DateUtil.now(),
                    DeliveryStatusesConstants.DISPATCHED,
                    providerStatus,
                    sms.getOpenMrsId(),
                    messageId,
                    null));
      }
    }
  }
}
