/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.handler.impl;

import org.connectforlife.module.sms.api.service.AutomaticResponseEvaluatorService;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.data.AutomaticResponseData;
import org.connectforlife.module.sms.api.handler.IncomingMessageData;
import org.connectforlife.module.sms.api.handler.IncomingMessageHandler;
import org.connectforlife.module.sms.api.service.SmsService;
import org.connectforlife.module.sms.api.util.AutomaticResponseEvaluationMessageContext;

import java.util.Optional;

/**
 * The default implementation of {@link IncomingMessageHandler}.
 *
 * <p>The handler sends a message to the incoming message's sender. The message is created by an
 * {@link AutomaticResponseEvaluatorService}.
 */
public class DefaultIncomingMessageHandler implements IncomingMessageHandler {
  private AutomaticResponseEvaluatorService automaticResponseEvaluatorService;
  private SmsService smsService;

  @Override
  public int priority() {
    return IncomingMessageHandler.BUILD_IN_PRIORITY;
  }

  @Override
  public boolean handle(IncomingMessageData message) {
    // Don't process repeated messages
    if (message.isReceivedForAFistTime()) {
      processMessages(message);
    }

    return false;
  }

  private void processMessages(IncomingMessageData message) {
    final Optional<AutomaticResponseData> automaticResponse =
        automaticResponseEvaluatorService.evaluate(
            new AutomaticResponseEvaluationMessageContext(message));

    if (automaticResponse.isPresent()) {
      final OutgoingSms automaticResponseSms =
          new OutgoingSms(
              message.getConfig().getName(),
              message.getSenderPhoneNumber(),
              automaticResponse.get().getMessage(),
              automaticResponse.get().getCustomParameters());
      smsService.send(automaticResponseSms);
    }
  }

  public void setAutomaticResponseEvaluatorService(
      AutomaticResponseEvaluatorService automaticResponseEvaluatorService) {
    this.automaticResponseEvaluatorService = automaticResponseEvaluatorService;
  }

  public void setSmsService(SmsService smsService) {
    this.smsService = smsService;
  }
}
