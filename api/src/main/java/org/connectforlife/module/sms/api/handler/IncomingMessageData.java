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

import java.util.Date;

/**
 * The immutable IncomingMessageData Class stores information about single message received by the
 * system.
 * @see IncomingMessageDataBuilder
 */
public final class IncomingMessageData {
	private final boolean receivedForAFistTime;
	private final Config config;
	private final String senderPhoneNumber;
	private final String message;
	private final Date receivedAt;
	private final String deliveryStatus;
	private final String providerMessageId;

	IncomingMessageData(
			boolean receivedForAFistTime,
			Config config,
			String senderPhoneNumber,
			String message,
			Date receivedAt,
			String deliveryStatus,
			String providerMessageId) {
		this.receivedForAFistTime = receivedForAFistTime;
		this.config = config;
		this.senderPhoneNumber = senderPhoneNumber;
		this.message = message;
		this.receivedAt = receivedAt!= null ? new Date(receivedAt.getTime()) : null;
		this.deliveryStatus = deliveryStatus;
		this.providerMessageId = providerMessageId;
	}

	public boolean isReceivedForAFistTime() {
		return receivedForAFistTime;
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

	public Date getReceivedAt() { return receivedAt; }

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public String getProviderMessageId() {
		return providerMessageId;
	}
}
