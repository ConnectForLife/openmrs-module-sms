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

import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.handler.IncomingMessageData;

import java.util.Date;

/**
 * The AutomaticResponseEvaluationContext Class.
 *
 * <p>The POJO with information related to the message which caused the Automatic Response.
 */
public class AutomaticResponseEvaluationMessageContext {
  private final Config config;
  private final String senderPhoneNumber;
  private final String message;
  private final Date receivedAt;
  private final String deliveryStatus;
  private final String providerMessageId;

  public AutomaticResponseEvaluationMessageContext(IncomingMessageData incomingMessage) {
    this.config = incomingMessage.getConfig();
    this.senderPhoneNumber = incomingMessage.getSenderPhoneNumber();
    this.message = incomingMessage.getMessage();
    this.receivedAt = incomingMessage.getReceivedAt();
    this.deliveryStatus = incomingMessage.getDeliveryStatus();
    this.providerMessageId = incomingMessage.getProviderMessageId();
  }

  public Config getConfig() {
    return config;
  }

  public String getSenderPhoneNumber() {
    return senderPhoneNumber;
  }

  public String getMessage() {
    return message;
  }

  public Date getReceivedAt() {
    return receivedAt;
  }

  public String getDeliveryStatus() {
    return deliveryStatus;
  }

  public String getProviderMessageId() {
    return providerMessageId;
  }
}
