/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.web;

import org.apache.commons.lang3.StringUtils;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecordSearchCriteria;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.hibernate.criterion.Order;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Models the audit log filter settings UI */
public class GridSettings {

	/** The default page to display. */
	private static final int DEFAULT_PAGE_INDEX = 1;

	/** The default value of number of rows to display per page. */
	private static final int DEFAULT_PAGE_SIZE = 100;

	/** The number of rows to display per page. */
	private Integer rows;

	/** The page to display, starting from 1. */
	private Integer page;

	/** The name of the column used for sorting. */
	private String sortColumn;

	/** The direction used for sorting, either asc or desc. */
	private String sortDirection;

	/** The name of the SMS configuration for which to retrieve records. */
	private String config;

	/** The phone number for which records should get retrieved. */
	private String phoneNumber;

	/** The content of the message to search for. */
	private String messageContent;

	/** The datetime describing the start of time range from which records should get retrieved. */
	private String timeFrom;

	/** The datetime describing the end of time range from which records should get retrieved. */
	private String timeTo;

	/** The delivery status to search for. */
	private String deliveryStatus;

	/** The provider status to search for. */
	private String providerStatus;

	/** The SMS direction to search for, either inbound or outbound. */
	private String smsDirection;

	/** The OpenMRS ID to search for. */
	private String openMrsId;

	/** The provider ID to search for. */
	private String providerId;

	/** The error message to search for */
	private String errorMessage;

	/** @return the number of rows to display per page */
	public Integer getRows() {
		return rows;
	}

	/** @param rows the number of rows to display per page */
	public void setRows(Integer rows) {
		this.rows = rows;
	}

	/** @return the page to display, starting from 1 */
	public Integer getPage() {
		return page;
	}

	/** @param page the page to display, starting from 1 */
	public void setPage(Integer page) {
		this.page = page;
	}

	/** @return the name of the column used for sorting */
	public String getSortColumn() {
		return sortColumn;
	}

	/** @param sortColumn the name of the column used for sorting */
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	/** @return the direction used for sorting, either asc or desc */
	public String getSortDirection() {
		return sortDirection;
	}

	/** @param sortDirection the direction used for sorting, either asc or desc */
	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	/** @return the name of the SMS configuration for which to retrieve records */
	public String getConfig() {
		return config;
	}

	/** @param config the name of the SMS configuration for which to retrieve records */
	public void setConfig(String config) {
		this.config = config;
	}

	/** @return the phone number for which records should get retrieved */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/** @param phoneNumber the phone number for which records should get retrieved */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/** @return the content of the message to search for */
	public String getMessageContent() {
		return messageContent;
	}

	/** @param messageContent the content of the message to search for */
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	/** @return the datetime describing the start of time range from which records should get retrieved */
	public String getTimeFrom() {
		return timeFrom;
	}

	/** @param timeFrom the datetime describing the start of time range from which records should get retrieved */
	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	/** @return the datetime describing the end of time range from which records should get retrieved */
	public String getTimeTo() {
		return timeTo;
	}

	/** @param timeTo the datetime describing the end of time range from which records should get retrieved */
	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	/** @return the delivery status to search for */
	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	/** @param deliveryStatus the delivery status to search for */
	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	/** @return the provider status to search for */
	public String getProviderStatus() {
		return providerStatus;
	}

	/** @param providerStatus the provider status to search for */
	public void setProviderStatus(String providerStatus) {
		this.providerStatus = providerStatus;
	}

	/** @return the SMS direction to search for, either inbound or outbound */
	public String getSmsDirection() {
		return smsDirection;
	}

	/** @param smsDirection the SMS direction to search for, either inbound or outbound */
	public void setSmsDirection(String smsDirection) {
		this.smsDirection = smsDirection;
	}

	/** @return the OpenMRS ID to search for */
	public String getOpenMrsId() {
		return openMrsId;
	}

	/** @param openMrsId the OpenMRS ID to search for */
	public void setOpenMrsId(String openMrsId) {
		this.openMrsId = openMrsId;
	}

	/** @return the provider ID to search for */
	public String getProviderId() {
		return providerId;
	}

	/** @param providerId the provider ID to search for */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/** @return the error message to search for */
	public String getErrorMessage() {
		return errorMessage;
	}

	/** @param errorMessage the error message to search for */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Converts these grid settings to a {@link SmsRecordSearchCriteria} object, which
	 * contains type safe information and should be used for building database criteria.
	 * @return the newly created search crtieria
	 */
	public SmsRecordSearchCriteria toSmsRecordSearchCriteria() {
		boolean reverse = "desc".equalsIgnoreCase(sortDirection);
		Order order = null;
		if (StringUtils.isNotBlank(sortColumn)) {
			order = reverse ? Order.desc(sortColumn) : Order.asc(sortColumn);
		}

		Set<SmsDirection> directions = getSmsDirectionFromSettings();
		Set<String> deliveryStatuses = getDeliveryStatusFromSettings();
		Interval range = createRangeFromSettings();
		return new SmsRecordSearchCriteria()
						.withSmsDirections(directions)
						.withConfig(config)
						.withPhoneNumber(phoneNumber)
						.withMessageContent(messageContent)
						.withTimestampRange(range)
						.withProviderStatus(providerStatus)
						.withDeliveryStatuses(deliveryStatuses)
						.withOpenMrsId(openMrsId)
						.withProviderId(providerId)
						.withErrorMessage(errorMessage)
						.withOrder(order);
	}

	/**
	 * Converts these grid settings to a {@link PagingInfo} object, which
	 * contains the paging configuration.
	 * @return the newly created paging information
	 */
	public PagingInfo toPageInfo() {
		Integer pageIndex = getPage();
		Integer pageSize = getRows();
		return new PagingInfo(pageIndex != null ? pageIndex : DEFAULT_PAGE_INDEX,
						pageSize != null ? pageSize : DEFAULT_PAGE_SIZE);
	}

	private Set<SmsDirection> getSmsDirectionFromSettings() {
		Set<SmsDirection> smsDirections = new HashSet<>();
		if (StringUtils.isNotBlank(smsDirection)) {
			if (smsDirection.contains(",")) {
				String[] smsDirectionList = smsDirection.split(",");
				for (String type : smsDirectionList) {
					if (!type.isEmpty()) {
						smsDirections.add(SmsDirection.valueOf(type));
					}
				}
			} else {
				smsDirections = Collections.singleton(SmsDirection.valueOf(smsDirection));
			}
		}
		return smsDirections;
	}

	private Set<String> getDeliveryStatusFromSettings() {
		Set<String> smsDeliveryStatus = new HashSet<>();
		if (StringUtils.isNotBlank(deliveryStatus)) {
			if (deliveryStatus.contains(",")) {
				smsDeliveryStatus = new HashSet<>(Arrays.asList(deliveryStatus.split(",")));
			} else {
				smsDeliveryStatus = Collections.singleton(deliveryStatus);
			}
		}
		return smsDeliveryStatus;
	}

	private Interval createRangeFromSettings() {
		return new Interval(timeFrom, timeTo);
	}

	@Override
	public String toString() {
		return "GridSettings{" +
						"rows=" + rows +
						", page=" + page +
						", sortColumn='" + sortColumn + '\'' +
						", sortDirection='" + sortDirection + '\'' +
						", config='" + config + '\'' +
						", phoneNumber='" + phoneNumber + '\'' +
						", messageContent='" + messageContent + '\'' +
						", timeFrom='" + timeFrom + '\'' +
						", timeTo='" + timeTo + '\'' +
						", deliveryStatus='" + deliveryStatus + '\'' +
						", providerStatus='" + providerStatus + '\'' +
						", smsDirection='" + smsDirection + '\'' +
						", openMrsId='" + openMrsId + '\'' +
						", providerId='" + providerId + '\'' +
						'}';
	}
}
