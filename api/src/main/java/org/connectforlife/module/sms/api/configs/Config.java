/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.configs;

import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A connection to a particular SMS provider, there may be more than one config for a given provider
 * and/or multiple connections to multiple providers. But realistically, most implementations will
 * have one config.
 */
public class Config {

  /** The unique name identifying the configuration. */
  private String name;

  /**
   * The maximum number of retries that will be performed when sending an SMS. If there was an error
   * when sending the SMS, we will initiate a retry.
   */
  private Integer maxRetries;

  /** Whether to exclude the split footer in the last part of a split SMS message. */
  private Boolean excludeLastFooter;

  /** The header that will be included in split SMS messages. */
  private String splitHeader;

  /** The footer that will be included in split SMS messages. */
  private String splitFooter;

  /** The name of the configuration template for this config. */
  private String templateName;

  /** Additional configuration properties that will be always passed to the template. */
  private List<ConfigProp> props = new ArrayList<>();

  /** The script used to determine an automatic response for messages received using this config. */
  private String automaticResponseScript;

  /** @return the unique name identifying the configuration */
  public String getName() {
    return name;
  }

  /** @param name the unique name identifying the configuration */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the maximum number of retries that will be performed when sending an SMS. If there was an
   * error when sending the SMS, we will initiate a retry.
   *
   * @return the maximum number of retries
   */
  public Integer getMaxRetries() {
    return maxRetries;
  }

  /**
   * Sets the maximum number of retries that will be performed when sending an SMS. If there was an
   * error when sending the SMS, we will initiate a retry.
   *
   * @param maxRetries the maximum number of retries
   */
  public void setMaxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
  }

  /**
   * @return true to exclude the split footer in the last part of a split SMS message, false
   *     otherwise
   */
  public Boolean getExcludeLastFooter() {
    return excludeLastFooter;
  }

  /**
   * @param excludeLastFooter true to exclude the split footer in the last part of a split SMS
   *     message, false otherwise
   */
  public void setExcludeLastFooter(Boolean excludeLastFooter) {
    this.excludeLastFooter = excludeLastFooter;
  }

  /** @return the header that will be included in split SMS messages */
  public String getSplitHeader() {
    return splitHeader;
  }

  /** @param splitHeader the header that will be included in split SMS messages */
  public void setSplitHeader(String splitHeader) {
    this.splitHeader = splitHeader;
  }

  /** @return the footer that will be included in split SMS messages */
  public String getSplitFooter() {
    return splitFooter;
  }

  /** @param splitFooter the footer that will be included in split SMS messages */
  public void setSplitFooter(String splitFooter) {
    this.splitFooter = splitFooter;
  }

  /** @return the name of the configuration template for this config */
  public String getTemplateName() {
    return templateName;
  }

  /** @param templateName the name of the configuration template for this config */
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  /** @return additional configuration properties that will be always passed to the template */
  public List<ConfigProp> getProps() {
    return props;
  }

  /** @param props additional configuration properties that will be always passed to the template */
  public void setProps(List<ConfigProp> props) {
    this.props = props;
  }

  public String getAutomaticResponseScript() {
    return automaticResponseScript;
  }

  public void setAutomaticResponseScript(String automaticResponseScript) {
    this.automaticResponseScript = automaticResponseScript;
  }

  /**
   * Returns an appropriate Delivery Status for the given failure count. If the failure count is
   * higher than the maximum number of retries for this configuration, then ABORTED Status is
   * returned. Otherwise RETRYING Status is returned, indicating the retries are ongoing.
   *
   * @param failureCount the failure count to check
   * @return the delivery status matching the failure count
   */
  public String retryOrAbortStatus(Integer failureCount) {
    if (failureCount < maxRetries) {
      return DeliveryStatusesConstants.RETRYING;
    }
    return DeliveryStatusesConstants.ABORTED;
  }

  /**
   * Returns an appropriate subject for an event indicating an SMS send failure. If the failure
   * count is higher than the maximum number of retries for this configuration, then a subject
   * indicating that to abort the SMS will be sent.
   *
   * @param failureCount the failure count to check
   * @return the subject for the event
   */
  public String retryOrAbortSubject(Integer failureCount) {
    if (failureCount < maxRetries) {
      return SmsEventSubjectsConstants.RETRYING;
    }
    return SmsEventSubjectsConstants.ABORTED;
  }
}
