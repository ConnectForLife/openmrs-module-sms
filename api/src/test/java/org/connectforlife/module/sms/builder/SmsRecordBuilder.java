/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.builder;

import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.util.DateUtil;

import java.util.Date;
import java.util.UUID;

public class SmsRecordBuilder extends AbstractBuilder<SmsRecord> {

  private Integer id;
  private String config;
  private SmsDirection smsDirection;
  private String phoneNumber;
  private String messageContent;
  private Date timestamp;
  private String deliveryStatus;
  private String providerStatus;
  private String openMrsId;
  private String providerId;
  private String errorMessage;

  public SmsRecordBuilder() {
    this.id = this.getInstanceNumber();
    this.config = "nexmo";
    this.smsDirection = SmsDirection.OUTBOUND;
    this.phoneNumber = "35235567243";
    this.messageContent = "test message";
    this.timestamp = DateUtil.now();
    this.deliveryStatus = DeliveryStatusesConstants.DISPATCHED;
    this.providerStatus = "0";
    this.openMrsId = UUID.randomUUID().toString();
    this.providerId = "0A0000000123ABCD1";
    this.errorMessage = null;
  }

  @Override
  public SmsRecord build() {
    SmsRecord smsRecord = new SmsRecord();
    smsRecord.setId(this.id);
    smsRecord.setConfig(this.config);
    smsRecord.setSmsDirection(this.smsDirection);
    smsRecord.setPhoneNumber(this.phoneNumber);
    smsRecord.setMessageContent(this.messageContent);
    smsRecord.setTimestamp(this.timestamp);
    smsRecord.setDeliveryStatus(this.deliveryStatus);
    smsRecord.setProviderStatus(this.providerStatus);
    smsRecord.setOpenMrsId(this.openMrsId);
    smsRecord.setProviderId(this.providerId);
    smsRecord.setErrorMessage(this.errorMessage);
    return smsRecord;
  }

  @Override
  public SmsRecord buildAsNew() {
    return withId(null).build();
  }

  public Integer getId() {
    return id;
  }

  public SmsRecordBuilder withId(Integer id) {
    this.id = id;
    return this;
  }

  public String getConfig() {
    return config;
  }

  public SmsRecordBuilder withConfig(String config) {
    this.config = config;
    return this;
  }

  public SmsDirection getSmsDirection() {
    return smsDirection;
  }

  public SmsRecordBuilder withSmsDirection(SmsDirection smsDirection) {
    this.smsDirection = smsDirection;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public SmsRecordBuilder withPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public String getMessageContent() {
    return messageContent;
  }

  public SmsRecordBuilder withMessageContent(String messageContent) {
    this.messageContent = messageContent;
    return this;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public SmsRecordBuilder withTimestamp(Date timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public String getDeliveryStatus() {
    return deliveryStatus;
  }

  public SmsRecordBuilder withDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
    return this;
  }

  public String getProviderStatus() {
    return providerStatus;
  }

  public SmsRecordBuilder withProviderStatus(String providerStatus) {
    this.providerStatus = providerStatus;
    return this;
  }

  public String getOpenMrsId() {
    return openMrsId;
  }

  public SmsRecordBuilder withOpenMrsId(String openMrsId) {
    this.openMrsId = openMrsId;
    return this;
  }

  public String getProviderId() {
    return providerId;
  }

  public SmsRecordBuilder withProviderId(String providerId) {
    this.providerId = providerId;
    return this;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public SmsRecordBuilder withErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }
}
