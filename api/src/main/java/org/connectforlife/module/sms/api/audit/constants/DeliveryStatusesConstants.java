/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.audit.constants;

/** Utility class for storing delivery statuses. */
public final class DeliveryStatusesConstants {

  public static final String RECEIVED = "RECEIVED";
  public static final String RETRYING = "RETRYING";
  public static final String ABORTED = "ABORTED";
  public static final String DISPATCHED = "DISPATCHED";
  public static final String FAILURE_CONFIRMED = "FAILURE_CONFIRMED";
  public static final String SCHEDULED = "SCHEDULED";
  public static final String PENDING = "PENDING";

  private DeliveryStatusesConstants() {}
}
