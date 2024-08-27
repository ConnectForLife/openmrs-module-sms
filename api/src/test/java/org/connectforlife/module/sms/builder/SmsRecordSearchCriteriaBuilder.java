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

import org.hibernate.criterion.Order;
import org.connectforlife.module.sms.TestConstants;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecordSearchCriteria;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.web.Interval;

import java.util.HashSet;
import java.util.Set;

public class SmsRecordSearchCriteriaBuilder extends AbstractBuilder<SmsRecordSearchCriteria> {

  private Interval interval;
  private String config;
  private HashSet<SmsDirection> directions;
  private Set<String> statuses;
  private String providerStatus;
  private Order order;
  private String phoneNumber;
  private String messageContent;
  private String errorMessage;

  public SmsRecordSearchCriteriaBuilder() {
    this.interval = new IntervalBuilder().build();
    this.config = TestConstants.CONFIG;
    this.directions = buildDirections();
    this.statuses = buildStatuses();
    this.providerStatus = TestConstants.PROVIDER_STATUS;
    this.order = Order.desc(TestConstants.SORT_COLUMN);
    this.phoneNumber = TestConstants.PHONE_NUMBER;
    this.messageContent = TestConstants.MESSAGE_CONTENT;
    this.errorMessage = TestConstants.ERROR_MESSAGE;
  }

  @Override
  public SmsRecordSearchCriteria build() {
    SmsRecordSearchCriteria searchCriteria = new SmsRecordSearchCriteria();
    searchCriteria.withTimestampRange(this.interval);
    searchCriteria.withConfig(this.config);
    searchCriteria.withSmsDirections(this.directions);
    searchCriteria.withDeliveryStatuses(this.statuses);
    searchCriteria.withProviderStatus(this.providerStatus);
    searchCriteria.withOrder(this.order);
    searchCriteria.withPhoneNumber(this.phoneNumber);
    searchCriteria.withMessageContent(this.messageContent);
    searchCriteria.withErrorMessage(this.errorMessage);
    return searchCriteria;
  }

  @Override
  public SmsRecordSearchCriteria buildAsNew() {
    return null;
  }

  public Interval getInterval() {
    return interval;
  }

  public SmsRecordSearchCriteriaBuilder withInterval(Interval interval) {
    this.interval = interval;
    return this;
  }

  public String getConfig() {
    return config;
  }

  public SmsRecordSearchCriteriaBuilder withConfig(String config) {
    this.config = config;
    return this;
  }

  public HashSet<SmsDirection> getDirections() {
    return directions;
  }

  public SmsRecordSearchCriteriaBuilder withDirections(HashSet<SmsDirection> directions) {
    this.directions = directions;
    return this;
  }

  public Set<String> getStatuses() {
    return statuses;
  }

  public SmsRecordSearchCriteriaBuilder withStatuses(Set<String> statuses) {
    this.statuses = statuses;
    return this;
  }

  public String getProviderStatus() {
    return providerStatus;
  }

  public SmsRecordSearchCriteriaBuilder withProviderStatus(String providerStatus) {
    this.providerStatus = providerStatus;
    return this;
  }

  public Order getOrder() {
    return order;
  }

  public SmsRecordSearchCriteriaBuilder withOrder(Order order) {
    this.order = order;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public SmsRecordSearchCriteriaBuilder withPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public String getMessageContent() {
    return messageContent;
  }

  public SmsRecordSearchCriteriaBuilder withMessageContent(String messageContent) {
    this.messageContent = messageContent;
    return this;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public SmsRecordSearchCriteriaBuilder withErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  private HashSet<SmsDirection> buildDirections() {
    HashSet<SmsDirection> result = new HashSet<>();
    result.add(SmsDirection.OUTBOUND);
    result.add(SmsDirection.INBOUND);
    return result;
  }

  private Set<String> buildStatuses() {
    HashSet<String> results = new HashSet<>();
    results.add(DeliveryStatusesConstants.DISPATCHED);
    results.add(DeliveryStatusesConstants.ABORTED);
    return results;
  }
}
