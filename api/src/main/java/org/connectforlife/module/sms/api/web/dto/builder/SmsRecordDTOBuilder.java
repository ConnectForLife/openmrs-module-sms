/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.web.dto.builder;

import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.web.dto.SmsRecordDTO;
import org.openmrs.User;

public class SmsRecordDTOBuilder {

	private Integer id;

	private String errorMessage;

	private String providerStatus;

	private String openMrsId;

	private String providerId;

	private String deliveryStatus;

	private String messageContent;

	private String timestamp;

	private String config;

	private String smsDirection;

	private String phoneNumber;

	private String modificationDate;

	private String creationDate;

	private String modifiedBy;

	private String creator;

	public SmsRecordDTOBuilder setId(Integer id) {
		this.id = id;
		return this;
	}

	public SmsRecordDTOBuilder setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public SmsRecordDTOBuilder setProviderStatus(String providerStatus) {
		this.providerStatus = providerStatus;
		return this;
	}

	public SmsRecordDTOBuilder setOpenMrsId(String openMrsId) {
		this.openMrsId = openMrsId;
		return this;
	}

	public SmsRecordDTOBuilder setProviderId(String providerId) {
		this.providerId = providerId;
		return this;
	}

	public SmsRecordDTOBuilder setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
		return this;
	}

	public SmsRecordDTOBuilder setMessageContent(String messageContent) {
		this.messageContent = messageContent;
		return this;
	}

	public SmsRecordDTOBuilder setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public SmsRecordDTOBuilder setConfig(String config) {
		this.config = config;
		return this;
	}

	public SmsRecordDTOBuilder setSmsDirection(SmsDirection smsDirection) {
		this.smsDirection = smsDirection != null ? smsDirection.name() : null;
		return this;
	}

	public SmsRecordDTOBuilder setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public SmsRecordDTOBuilder setModificationDate(String modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public SmsRecordDTOBuilder setCreationDate(String creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public SmsRecordDTOBuilder setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy != null ? modifiedBy.getUsername() : null;
		return this;
	}

	public SmsRecordDTOBuilder setCreator(User creator) {
		this.creator = creator != null ? creator.getUsername() : null;
		return this;
	}

	public SmsRecordDTO createSmsRecordDTO() {
		return new SmsRecordDTO(id, errorMessage, providerStatus, openMrsId, providerId, deliveryStatus, messageContent,
				timestamp, config, smsDirection, phoneNumber, modificationDate, creationDate, modifiedBy, creator);
	}
}
