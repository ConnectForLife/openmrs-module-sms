/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.handler;

import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.util.IncomingMessageAccessor;

import java.util.Date;

/** The IncomingMessageDataBuilder Class, a builder for {@link IncomingMessageData}. */
public final class IncomingMessageDataBuilder {
  private boolean receivedForAFistTime = true;
  private Config config;
  private String sender;
  private String message;
  private Date receivedAt;
  private String deliveryStatus;
  private String providerMessageId;

  public IncomingMessageDataBuilder() {}

  public IncomingMessageDataBuilder(IncomingMessageAccessor messageAccessor) {
    sender = messageAccessor.getSender();
    message = messageAccessor.getMessage();
    deliveryStatus = messageAccessor.getStatus();
    providerMessageId = messageAccessor.getMsgId();
  }

  public IncomingMessageData build() {
    return new IncomingMessageData(receivedForAFistTime, config, sender, message, receivedAt, deliveryStatus, providerMessageId);
  }

  public IncomingMessageDataBuilder setReceivedForAFistTime(boolean receivedForAFistTime) {
    this.receivedForAFistTime = receivedForAFistTime;
    return this;
  }

  public IncomingMessageDataBuilder setConfig(Config config) {
    this.config = config;
    return this;
  }

  public IncomingMessageDataBuilder setSender(String sender) {
    this.sender = sender;
    return this;
  }

  public IncomingMessageDataBuilder setMessage(String message) {
    this.message = message;
    return this;
  }

  public IncomingMessageDataBuilder setReceivedAt(Date receivedAt) {
    this.receivedAt = receivedAt!= null ? new Date(receivedAt.getTime()) : null;
    return this;
  }

  public IncomingMessageDataBuilder setDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
    return this;
  }

  public IncomingMessageDataBuilder setProviderMessageId(String providerMessageId) {
    this.providerMessageId = providerMessageId;
    return this;
  }
}
