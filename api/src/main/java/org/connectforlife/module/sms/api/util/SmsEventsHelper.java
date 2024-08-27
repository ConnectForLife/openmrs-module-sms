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

import org.connectforlife.module.sms.api.event.SmsEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** SmsEvent Helper class, builds events from provided parameters. */
public final class SmsEventsHelper {

  /**
   * Creates an OpenMRS event which will describe an outbound SMS (already sent or to be sent).
   *
   * @param subject the subject of the evnet
   * @param config the configuration which
   * @param recipients the recipient numbers for the SMS
   * @param message the SMS message
   * @param openMrsId the OpenMRS ID of the message
   * @param providerMessageId the provider ID of the message
   * @param failureCount the failure counter for this message, will be used for retries
   * @param providerStatus the SMS status coming from the provider
   * @param timestamp the timestamp of the message
   * @param customParams custom parameters for the provider
   * @return a {@link SmsEvent} describing the outbound SMS
   */
  // CHECKSTYLE:OFF: ParameterNumber
  @SuppressWarnings({"PMD.ExcessiveParameterList"})
  public static SmsEvent outboundEvent(
      String subject,
      String config, // NO CHECKSTYLE ParameterNumber
      List<String> recipients,
      String message,
      String openMrsId,
      String providerMessageId,
      Integer failureCount,
      String providerStatus,
      Date timestamp,
      Map<String, Object> customParams) {
    Map<String, Object> params = new HashMap<>();
    params.put(SmsEventParamsConstants.CONFIG, config);
    params.put(SmsEventParamsConstants.RECIPIENTS, recipients);
    params.put(SmsEventParamsConstants.MESSAGE, message);
    params.put(SmsEventParamsConstants.OPENMRS_ID, openMrsId);
    if (providerMessageId != null) {
      params.put(SmsEventParamsConstants.PROVIDER_MESSAGE_ID, providerMessageId);
    }
    if (failureCount != null) {
      params.put(SmsEventParamsConstants.FAILURE_COUNT, failureCount);
    } else {
      params.put(SmsEventParamsConstants.FAILURE_COUNT, 0);
    }
    if (providerStatus != null) {
      params.put(SmsEventParamsConstants.PROVIDER_STATUS, providerStatus);
    }
    if (timestamp == null) {
      params.put(SmsEventParamsConstants.TIMESTAMP, DateUtil.now());
    } else {
      params.put(SmsEventParamsConstants.TIMESTAMP, timestamp);
    }
    if (customParams != null && !customParams.isEmpty()) {
      params.put(SmsEventParamsConstants.CUSTOM_PARAMS, customParams);
    }
    return new SmsEvent(subject, params);
  }
  // CHECKSTYLE:ON: ParameterNumber

  private SmsEventsHelper() {}
}
