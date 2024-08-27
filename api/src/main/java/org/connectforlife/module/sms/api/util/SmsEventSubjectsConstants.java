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

/** Event subjects, mirrors DeliveryStatus */
public final class SmsEventSubjectsConstants {

  private SmsEventSubjectsConstants() {}

  public static final String PENDING = "outbound_sms_pending";
  public static final String RETRYING = "outbound_sms_retrying";
  public static final String ABORTED = "outbound_sms_aborted";
  public static final String SCHEDULED = "outbound_sms_scheduled";
  public static final String DISPATCHED = "outbound_sms_dispatched";
  public static final String DELIVERY_CONFIRMED = "outbound_sms_delivery_confirmed";
  public static final String FAILURE_CONFIRMED = "outbound_sms_failure_confirmed";
  public static final String SEND_SMS = "send_sms";
  public static final String INBOUND_SMS = "inbound_sms";
}
