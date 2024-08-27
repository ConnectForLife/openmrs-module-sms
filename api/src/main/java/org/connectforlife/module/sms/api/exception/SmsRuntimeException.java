/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.exception;

public class SmsRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 4513932642755534409L;

    public SmsRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public SmsRuntimeException(String message) {
        super(message);
    }

    public SmsRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}