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

import org.connectforlife.module.sms.TestConstants;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.web.GridSettings;

public class GridSettingsBuilder extends AbstractBuilder<GridSettings> {

  private static final int ROWS = 23;
  private static final int PAGE = 2;
  private String timeFrom;
  private String timeTo;
  private String config;
  private String smsDirection;
  private String deliveryStatus;
  private String providerStatus;
  private String sortDirection;
  private String sortColumn;
  private String phoneNumber;
  private String messageContent;
  private String errorMessage;
  private Integer row;
  private Integer page;

  public GridSettingsBuilder() {
    this.timeFrom = TestConstants.TIME_FROM;
    this.timeTo = TestConstants.TIME_TO;
    this.config = TestConstants.CONFIG;
    this.smsDirection = buildSmsDirection();
    this.deliveryStatus =
        DeliveryStatusesConstants.DISPATCHED + "," + DeliveryStatusesConstants.ABORTED;
    this.providerStatus = TestConstants.PROVIDER_STATUS;
    this.sortDirection = TestConstants.DESC;
    this.sortColumn = TestConstants.SORT_COLUMN;
    this.phoneNumber = TestConstants.PHONE_NUMBER;
    this.messageContent = TestConstants.MESSAGE_CONTENT;
    this.errorMessage = TestConstants.ERROR_MESSAGE;
    this.row = ROWS;
    this.page = PAGE;
  }

  @Override
  public GridSettings build() {
    GridSettings settings = new GridSettings();
    settings.setTimeFrom(this.timeFrom);
    settings.setTimeTo(this.timeTo);
    settings.setConfig(this.config);
    settings.setSmsDirection(this.smsDirection);
    settings.setDeliveryStatus(this.deliveryStatus);
    settings.setProviderStatus(this.providerStatus);
    settings.setSortDirection(this.sortDirection);
    settings.setSortColumn(this.sortColumn);
    settings.setPhoneNumber(this.phoneNumber);
    settings.setMessageContent(this.messageContent);
    settings.setErrorMessage(this.errorMessage);
    settings.setRows(this.row);
    settings.setPage(this.page);
    return settings;
  }

  @Override
  public GridSettings buildAsNew() {
    return build();
  }

  public String getTimeFrom() {
    return timeFrom;
  }

  public GridSettingsBuilder withTimeFrom(String timeFrom) {
    this.timeFrom = timeFrom;
    return this;
  }

  public String getTimeTo() {
    return timeTo;
  }

  public GridSettingsBuilder withTimeTo(String timeTo) {
    this.timeTo = timeTo;
    return this;
  }

  public String getConfig() {
    return config;
  }

  public GridSettingsBuilder withConfig(String config) {
    this.config = config;
    return this;
  }

  public String getSmsDirection() {
    return smsDirection;
  }

  public GridSettingsBuilder withSmsDirection(String smsDirection) {
    this.smsDirection = smsDirection;
    return this;
  }

  public String getDeliveryStatus() {
    return deliveryStatus;
  }

  public GridSettingsBuilder withDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
    return this;
  }

  public String getProviderStatus() {
    return providerStatus;
  }

  public GridSettingsBuilder withProviderStatus(String providerStatus) {
    this.providerStatus = providerStatus;
    return this;
  }

  public String getSortDirection() {
    return sortDirection;
  }

  public GridSettingsBuilder withSortDirection(String sortDirection) {
    this.sortDirection = sortDirection;
    return this;
  }

  public String getSortColumn() {
    return sortColumn;
  }

  public GridSettingsBuilder withSortColumn(String sortColumn) {
    this.sortColumn = sortColumn;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public GridSettingsBuilder withPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public String getMessageContent() {
    return messageContent;
  }

  public GridSettingsBuilder withMessageContent(String messageContent) {
    this.messageContent = messageContent;
    return this;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public GridSettingsBuilder withErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public Integer getRow() {
    return row;
  }

  public GridSettingsBuilder withRow(Integer row) {
    this.row = row;
    return this;
  }

  public Integer getPage() {
    return page;
  }

  public GridSettingsBuilder withPage(Integer page) {
    this.page = page;
    return this;
  }

  private String buildSmsDirection() {
    return SmsDirection.OUTBOUND.name() + "," + SmsDirection.INBOUND.name();
  }
}
