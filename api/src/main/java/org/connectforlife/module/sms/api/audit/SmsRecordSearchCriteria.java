/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.audit;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.connectforlife.module.sms.api.web.Interval;


/**
 * Helper used to generate a database lookup from log filter UI
 */
public class SmsRecordSearchCriteria {

    /**
     * The set of directions (inbound, outbound).
     */
    private Set<SmsDirection> smsDirections = new HashSet<>();

    /**
     * The name of the configuration associated with the SMS message.
     */
    private String config;

    /**
     * The number of the phone the message was received from or delivered to.
     */
    private String phoneNumber;

    /**
     * The contents of the SMS message.
     */
    private String messageContent;

    /**
     * The date-time range the timestamp of the SMS should fall into.
     */
    private Interval timestampRange;

    private String providerStatus;

    /**
     * The set of delivery status for the messages.
     */
    private Set<String> deliveryStatuses = new HashSet<>();

    /**
     * The id by which OpenMRS identifies the message.
     */
    private String openMrsId;

    /**
     * The provider generated ID for the SMS.
     */
    private String providerId;

    /**
     * The error message for the SMS.
     */
    private String errorMessage;

    private Order order;

    /**
     * Sets the sms directions which should be included in the query.
     *
     * @param smsDirections the set of directions (inbound, outbound)
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withSmsDirections(Set<SmsDirection> smsDirections) {
        this.smsDirections.addAll(smsDirections);
        return this;
    }

    /**
     * Sets the phone number part of the search.
     *
     * @param phoneNumber the phone number which received or sent the SMS
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    /**
     * Sets the configuration name part of the search.
     *
     * @param config the config name associated with the SMS
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withConfig(String config) {
        this.config = config;
        return this;
    }

    /**
     * Sets the OpenMRS ID part of the search query.
     *
     * @param openMrsId the ID used by OpenMRS to identify this SMS message
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withOpenMrsId(String openMrsId) {
        this.openMrsId = openMrsId;
        return this;
    }

    /**
     * Sets the provider ID part of the search query.
     *
     * @param providerId the ID used by the provider to identify this SMS message
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    /**
     * Sets the message content part of this search query.
     *
     * @param messageContent the content of the SMS
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withMessageContent(String messageContent) {
        this.messageContent = messageContent;
        return this;
    }

    /**
     * Sets the error message part of this search query.
     *
     * @param errorMessage the error message for the SMS
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    /**
     * Sets the expected timestamp for the messages retrieved by this query.
     *
     * @param timestamp the timestamp of the messages
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withTimestamp(Date timestamp) {
        this.timestampRange = new Interval(timestamp, timestamp);
        return this;
    }

    /**
     * Sets the expected timestamp range into which the messages retrieved by this query must fall into.
     *
     * @param timestampRange the timestamp range into which the messages must fall into
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withTimestampRange(Interval timestampRange) {
        this.timestampRange = timestampRange;
        return this;
    }

    public SmsRecordSearchCriteria withProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
        return this;
    }

    /**
     * Sets the delivery statuses the SMS messages retrieved by this search must match.
     *
     * @param deliveryStatuses the set of delivery status that will be taken into consideration when executing the query
     * @return this instance of the search criteria
     */
    public SmsRecordSearchCriteria withDeliveryStatuses(Set<String> deliveryStatuses) {
        this.deliveryStatuses.addAll(deliveryStatuses);
        return this;
    }

    public SmsRecordSearchCriteria withOrder(Order order) {
        this.order = order;
        return this;
    }

    // Getters

    public Set<SmsDirection> getSmsDirections() {
        return smsDirections;
    }

    /**
     * @return the configuration name which with the SMS should be associated with
     */
    public String getConfig() {
        return config;
    }

    /**
     * @return the phone number which received or sent the sms
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return the content of the sms message
     */
    public String getMessageContent() {
        return messageContent;
    }

    /**
     * @return the timestamp range into which SNS messages should fall
     */
    public Interval getTimestampRange() {
        return timestampRange;
    }

    /**
     * @return the set of expected delivery statuses for SMS messages
     */
    public Set<String> getDeliveryStatuses() {
        return deliveryStatuses;
    }

    /**
     * @return the ID by which OpenMRS identifies the SMS
     */
    public String getOpenMrsId() {
        return openMrsId;
    }

    /**
     * @return the ID by which the provider identifies the SMS
     */
    public String getProviderId() {
        return providerId;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    /**
     * @return the error message for the SMS
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public Order getOrder() {
        return order;
    }

    public void loadSearchCriteria(Criteria criteria) {
        addSetRestriction(criteria, "smsDirection", smsDirections);
        addStringRestriction(criteria, "config", config);
        addStringRestriction(criteria, "phoneNumber", phoneNumber);
        addStringRestriction(criteria, "messageContent", messageContent);
        addTimestampRestriction(criteria);
        addStringRestriction(criteria, "providerStatus", providerStatus);
        addSetRestriction(criteria, "deliveryStatus", deliveryStatuses);
        addStringRestriction(criteria, "openMrsId", openMrsId);
        addStringRestriction(criteria, "providerId", providerId);
        addStringRestriction(criteria, "errorMessage", errorMessage);
        addOrderRestriction(criteria);
    }

    private void addOrderRestriction(Criteria criteria) {
        if (order != null) {
            criteria.addOrder(order);
        }
    }

    private void addTimestampRestriction(Criteria criteria) {
        if (timestampRange != null) {
            criteria.add(Restrictions.between("timestamp", timestampRange.getFrom(), timestampRange.getTo()));
        }
    }

    private void addSetRestriction(Criteria criteria, String fieldName, Set<?> set) {
        if (set != null && !set.isEmpty()) {
            criteria.add(Restrictions.in(fieldName, set));
        }
    }

    private void addStringRestriction(Criteria criteria, String fieldName, String value) {
        if (StringUtils.isNotBlank(value)) {
            criteria.add(Restrictions.eq(fieldName, value));
        }
    }

    @Override
    public String toString() {
        return "SmsRecordSearchCriteria{" +
                "smsDirections=" + smsDirections +
                ", config='" + config + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", timestampRange=" + timestampRange +
                ", providerStatus='" + providerStatus + '\'' +
                ", deliveryStatuses=" + deliveryStatuses +
                ", openMrsId='" + openMrsId + '\'' +
                ", providerId='" + providerId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
