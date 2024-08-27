/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.builder;

import org.connectforlife.module.sms.TestConstants;
import org.connectforlife.module.sms.api.web.Interval;

public class IntervalBuilder extends AbstractBuilder<Interval> {

    private String timeFrom;
    private String timeTo;

    public IntervalBuilder() {
        this.timeFrom = TestConstants.TIME_FROM;
        this.timeTo = TestConstants.TIME_TO;
    }

    @Override
    public Interval build() {
        return new Interval(this.timeFrom, this.timeTo);
    }

    @Override
    public Interval buildAsNew() {
        return build();
    }
}
