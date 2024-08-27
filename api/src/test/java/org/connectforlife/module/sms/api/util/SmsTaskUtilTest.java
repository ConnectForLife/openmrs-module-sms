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

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SmsTaskUtilTest {

    private static final String STRING_62CHARS = RandomStringUtils.random(62, true, false);

    @Test
    public void shouldProperlyGenerateTaskName() {
        assertThat(SmsTaskUtil.generateTaskName("subject", "jobId"), equalTo("subject-jobId"));
    }

    @Test
    public void shouldShortenTaskNameIfLengthMoreThanMax() {
        assertThat(SmsTaskUtil.generateTaskName(STRING_62CHARS, "").length(), equalTo(SmsTaskUtil.NAME_MAX_LENGTH));
    }
}
