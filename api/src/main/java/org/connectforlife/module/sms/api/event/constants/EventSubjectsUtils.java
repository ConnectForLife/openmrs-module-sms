/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.event.constants;

/** Utility class for storing event subjects. */
public final class EventSubjectsUtils {

  private static final String BASE_SUBJECT = "org.openmrs.module.sms.api.api.";
  public static final String CONFIGS_CHANGED = BASE_SUBJECT + "configsChanged";

  private EventSubjectsUtils() {}
}
