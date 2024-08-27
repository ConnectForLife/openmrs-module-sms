/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.util;

/** Possible Sms Event payloads (ie: params) */
public final class SmsEventParamsConstants {

  private SmsEventParamsConstants() {}

  /** Config that was used for this message */
  public static final String CONFIG = "config";
  /** list of recipients (phone numbers) */
  public static final String RECIPIENTS = "recipients";
  /** incoming SMS recipient (phone number) */
  public static final String RECIPIENT = "recipient";
  /** incoming SMS sender (phone number) */
  public static final String SENDER = "sender";
  /** time at which this SMS should be sent */
  public static final String DELIVERY_TIME = "delivery_time";
  /** date and time when this event happened */
  public static final String TIMESTAMP = "timestamp";
  /** date and time when this event happened */
  public static final String TIMESTAMP_DATETIME = "timestamp_datetime";
  /** UTC time when this event happened */
  public static final String TIMESTAMP_TIME = "timestamp_time";
  /** the text content of the SMS message */
  public static final String MESSAGE = "message";
  /** internal SMS failure counter */
  public static final String FAILURE_COUNT = "failure_count";
  /** OpenMRS unique message id */
  public static final String OPENMRS_ID = "openmrs_id";
  /** provider unique message id */
  public static final String PROVIDER_MESSAGE_ID = "provider_message_id";
  /**
   * provider provided SMS delivery status, sometimes holds more information than what OpenMRS
   * models
   */
  public static final String PROVIDER_STATUS = "provider_status";
  /** map of custom parameters */
  public static final String CUSTOM_PARAMS = "custom_params";
}
