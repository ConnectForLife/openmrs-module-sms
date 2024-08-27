/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.service;

import org.apache.commons.lang.StringUtils;
import org.connectforlife.module.sms.api.event.SmsEvent;
import org.connectforlife.module.sms.api.util.SmsEventParamsConstants;

import java.util.*;

/** Represents an outgoing SMS */
public class OutgoingSms {

  /** One or more recipients */
  private List<String> recipients;
  /** The content of the message to send */
  private String message;
  /** If specified, use this config to send the SMS, otherwise use the default config */
  private String config;
  /** If specified will schedule the message for future delivery using the OpenMRS scheduler */
  private Date deliveryTime;
  /** OpenMRS specific message GUID */
  private String openMrsId;
  /** Provider specific message id */
  private String providerId;
  /** Internal failure counter */
  private Integer failureCount = 0;
  /** Map of custom parameters */
  private Map<String, Object> customParams;

  /** Constructs a new instance without setting any fields. */
  public OutgoingSms() {}

  /**
   * Constructs an instance using the field map from the provided event.
   *
   * @param smsEvent
   * @see SmsEventParamsConstants
   */
  public OutgoingSms(SmsEvent smsEvent) {
    config = smsEvent.getConfigParam();
    recipients = smsEvent.getRecipients();
    message = smsEvent.getMessage();
    deliveryTime = smsEvent.getDeliveryTime();
    if (smsEvent.paramsContainKey(SmsEventParamsConstants.FAILURE_COUNT)) {
      failureCount = smsEvent.getFailureCount();
    }
    openMrsId = smsEvent.getOpenMrsId();
    providerId = smsEvent.getProviderId();
    customParams = smsEvent.getCustomParams();
  }

  /**
   * Creates a new instance using the provided parameters.
   *
   * @param config use this config to send the SMS, otherwise use the default config
   * @param recipients one or more recipients
   * @param message the message to send
   * @param deliveryTime the expected delivery time of the sms
   */
  public OutgoingSms(String config, List<String> recipients, String message, Date deliveryTime) {
    this.recipients = recipients;
    this.message = message;
    this.config = config;
    this.deliveryTime = deliveryTime!= null ? new Date(deliveryTime.getTime()) : null;
  }

  /**
   * Creates a new instance using the provided parameters.
   *
   * @param config use this config to send the SMS, otherwise use the default config
   * @param recipient the recipient of the SMS
   * @param message the message to send
   * @param deliveryTime the expected delivery time of the sms
   */
  public OutgoingSms(String config, String recipient, String message, Date deliveryTime) {
    this(config, Collections.singletonList(recipient), message, deliveryTime);
  }

  /**
   * Creates a new instance using the provided parameters.
   *
   * @param config use this config to send the SMS, otherwise use the default config
   * @param recipients the recipients of the SMS
   * @param message the message to send
   */
  public OutgoingSms(String config, List<String> recipients, String message) {
    this.recipients = recipients;
    this.message = message;
    this.config = config;
  }

  public OutgoingSms(String config, String recipient, String message) {
    this(config, Collections.singletonList(recipient), message);
  }

  public OutgoingSms(String config, String recipient, String message, Map<String, Object> customParams) {
    this.config = config;
    this.message = message;
    this.recipients = Collections.singletonList(recipient);
    this.customParams = customParams;
  }

  /**
   * Creates a new instance using the provided parameters.
   *
   * @param recipients the recipients of the SMS
   * @param message the message to send
   * @param deliveryTime the expected delivery time of the sms
   */
  public OutgoingSms(List<String> recipients, String message, Date deliveryTime) {
    this.recipients = recipients;
    this.message = message;
    this.deliveryTime = deliveryTime!= null ? new Date(deliveryTime.getTime()) : null;
  }

  /**
   * Creates a new instance using the provided parameters.
   *
   * @param recipient the recipient of the SMS
   * @param message the message to send
   * @param deliveryTime the expected delivery time of the sms
   */
  public OutgoingSms(String recipient, String message, Date deliveryTime) {
    this(Collections.singletonList(recipient), message, deliveryTime);
  }

  /**
   * Creates a new instance using the provided parameters.
   *
   * @param recipients the recipients of the SMS
   * @param message the message to send
   */
  public OutgoingSms(List<String> recipients, String message) {
    this.recipients = recipients;
    this.message = message;
  }

  /**
   * Creates a new instance using the provided parameters.
   *
   * @param recipient the recipient of the SMS
   * @param message the message to send
   */
  public OutgoingSms(String recipient, String message) {
    this(Collections.singletonList(recipient), message);
  }

  /**
   * If specified, use this config to send the SMS, otherwise use the default config.
   *
   * @return the config that will be used for sending this SMS
   */
  public String getConfig() {
    return config;
  }

  /**
   * Checks if this SMS specifies a config for sending the SMS, or whether the default configuration
   * should be used.
   *
   * @return true if there's a config specified for this SMS, false if the default config is to be
   *     used
   */
  public Boolean hasConfig() {
    return StringUtils.isNotBlank(config);
  }

  /**
   * If specified, use this config to send the SMS, otherwise use the default config.
   *
   * @param config the config that will be used for sending this SMS
   */
  public void setConfig(String config) {
    this.config = config;
  }

  /**
   * Gets the recipients for this message. An SMS can have one or more recipients.
   *
   * @return the recipients fo the SMS
   */
  public List<String> getRecipients() {
    return recipients;
  }

  /**
   * Gets the recipients for this message. An SMS can have one or more recipients.
   *
   * @param recipients the recipients fo the SMS
   */
  public void setRecipients(List<String> recipients) {
    this.recipients = recipients;
  }

  /** @return the content of the message to send */
  public String getMessage() {
    return message;
  }

  /** @param message the content of the message to send */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the delivery time for this SMS. If specified, will schedule the message for future
   * delivery using the OpenMRS scheduler.
   *
   * @return the delivery time for this SMS
   */
  public Date getDeliveryTime() {
    return deliveryTime;
  }

  /**
   * Checks whether this SMS has a specified delivery time. If specified, will schedule the message
   * for future delivery using the OpenMRS scheduler. If not, the message should be immediately
   * delivered.
   *
   * @return true if this SMS has a specific delivery time, false otherwise
   */
  public Boolean hasDeliveryTime() {
    return deliveryTime != null;
  }

  /**
   * Gets the delivery time for this SMS. If specified will schedule the message for future delivery
   * using the OpenMRS scheduler.
   *
   * @param deliveryTime the delivery time for this SMS
   */
  public void setDeliveryTime(Date deliveryTime) {
    this.deliveryTime = deliveryTime!= null ? new Date(deliveryTime.getTime()) : null;
  }

  /**
   * Gets the failure counter, used by the SMS module to keep track of the failures and execute
   * retries.
   *
   * @return the internal failure counter for this SMS
   */
  public Integer getFailureCount() {
    return failureCount;
  }

  /**
   * Sets the failure counter, used by the SMS module to keep track of the failures and execute
   * retries.
   *
   * @param failureCount the internal failure counter for this SMS
   */
  public void setFailureCount(Integer failureCount) {
    this.failureCount = failureCount;
  }

  public Boolean hasOpenMrsId() {
    return StringUtils.isNotBlank(openMrsId);
  }

  /**
   * Gets the OpenMRS specific message GUID of this SMS.
   *
   * @return the OpenMrs specific GUID for this SMS
   */
  public String getOpenMrsId() {
    return openMrsId;
  }

  /**
   * Sets the OpenMRS specific message GUID of this SMS.
   *
   * @param openMrsId the OpenMrs specific GUID for this SMS
   */
  public void setOpenMrsId(String openMrsId) {
    this.openMrsId = openMrsId;
  }

  /** @return the provider specific message id */
  public String getProviderId() {
    return providerId;
  }

  /** @param providerId the provider specific message id */
  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  /** @return map of custom parameters for the template */
  public Map<String, Object> getCustomParams() {
    return customParams;
  }

  /** @param customParams map of custom parameters for the template */
  public void setCustomParams(Map<String, Object> customParams) {
    this.customParams = customParams;
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipients, message, deliveryTime);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final OutgoingSms other = (OutgoingSms) obj;

    return Objects.equals(this.config, other.config)
        && Objects.equals(this.recipients, other.recipients)
        && Objects.equals(this.message, other.message)
        && Objects.equals(this.deliveryTime, other.deliveryTime)
        && Objects.equals(this.failureCount, other.failureCount)
        && Objects.equals(this.openMrsId, other.openMrsId)
        && Objects.equals(this.providerId, other.providerId);
  }

  @Override
  public String toString() {
    return "OutgoingSms{"
        + "recipients="
        + recipients
        + ", message='"
        + message
        + '\''
        + ", config='"
        + config
        + '\''
        + ", deliveryTime="
        + deliveryTime
        + ", failureCount="
        + failureCount
        + ", openMrsId='"
        + openMrsId
        + '\''
        + ", providerId='"
        + providerId
        + '\''
        + '}';
  }
}
