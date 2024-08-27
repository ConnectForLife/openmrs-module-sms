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
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.SmsEventsHelper;

/**
 * Deals with providers who return multi-line responses, but return a different response when
 * sending only one message, like Clickatell does.
 */
public class MultilineSingleResponseHandler extends ResponseHandler {

  /**
   * Constructs an instance using the provided template and configuration.
   *
   * @param template the template to use
   * @param config the configuration to use
   */
  MultilineSingleResponseHandler(Template template, Config config) {
    super(template, config);
  }

  @Override
  public void handle(OutgoingSms sms, String response, Header[] headers) {

    String messageId = getTemplateOutgoingResponse().extractSingleSuccessMessageId(response);
    String providerStatus = getTemplateOutgoingResponse().extractProviderStatus(response);

    if (messageId == null) {
      Integer failureCount = sms.getFailureCount() + 1;

      String failureMessage = getTemplateOutgoingResponse().extractSingleFailureMessage(response);
      if (failureMessage == null) {
        failureMessage = response;
      }
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
      getLogger().info(String.format("Failed to send SMS: %s", failureMessage));
      getAuditRecords()
          .add(
              new SmsRecord(
                  getConfig().getName(),
                  SmsDirection.OUTBOUND,
                  sms.getRecipients().get(0),
                  sms.getMessage(),
                  DateUtil.now(),
                  getConfig().retryOrAbortStatus(failureCount),
                  providerStatus,
                  sms.getOpenMrsId(),
                  null,
                  failureMessage));
    } else {
      // todo: HIPAA concerns?
      getLogger()
          .info(
              String.format(
                  "Sent messageId %s '%s' to %s",
                  messageId, messageForLog(sms), sms.getRecipients().get(0)));
      getAuditRecords()
          .add(
              new SmsRecord(
                  getConfig().getName(),
                  SmsDirection.OUTBOUND,
                  sms.getRecipients().get(0),
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
