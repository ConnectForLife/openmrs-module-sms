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

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.connectforlife.module.sms.api.util.PrivilegeConstants;

/**
 * Service that allows sending SMS messages.
 */
public interface SmsService extends OpenmrsService {

    /**
     * This method allows sending outgoing sms messages through HTTP. The configuration specified in the {@link OutgoingSms}
     * object will be used for dealing with the provider.
     *
     * @param message the representation of the sms to send
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    void send(final OutgoingSms message);
}
