/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.http.SmsHttpService;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;

import java.util.Arrays;
import java.util.Map;

public class StatusSmsEventListener extends AbstractSmsEventListener {

    private static final Log LOGGER = LogFactory.getLog(StatusSmsEventListener.class);

    @Override
    public String[] getSubjects() {
        return new String[]{SmsEventSubjectsConstants.PENDING, SmsEventSubjectsConstants.SCHEDULED, SmsEventSubjectsConstants.RETRYING};
    }

    @Override
    protected void handleEvent(Map<String, Object> properties) {
        LOGGER.info(String.format("Handling external event %s: %s", Arrays.toString(getSubjects()),
                properties.get("message").toString().replace("\n", "\\n")));
        getComponent("sms.SmsHttpService", SmsHttpService.class)
                .send(new OutgoingSms(new SmsEvent(properties)));
    }
}
