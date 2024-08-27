/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.http;

import org.apache.commons.httpclient.Header;
import org.apache.commons.lang3.ArrayUtils;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.SmsEventsHelper;

import java.util.Collections;
import java.util.List;

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

      if (ArrayUtils.isEmpty(messageIdAndRecipient)) {
        Integer failureCount = sms.getFailureCount() + 1;
        String[] messageAndRecipient;

        messageAndRecipient =
            getTemplateOutgoingResponse().extractFailureMessageAndRecipient(responseLine);
        if (ArrayUtils.isEmpty(messageAndRecipient)) {
          getEvents()
              .add(
                  SmsEventsHelper.outboundEvent(
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
                      SmsDirection.OUTBOUND,
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
                  SmsEventsHelper.outboundEvent(
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
                      SmsDirection.OUTBOUND,
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
                    SmsDirection.OUTBOUND,
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
