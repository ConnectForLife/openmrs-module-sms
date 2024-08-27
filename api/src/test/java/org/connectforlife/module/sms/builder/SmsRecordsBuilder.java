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

import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.SmsRecords;

import java.util.ArrayList;
import java.util.List;

public class SmsRecordsBuilder extends AbstractBuilder<SmsRecords> {

    private static final String OTHER_MESSAGE_2 = "other message 2";
    private static final String OTHER_MESSAGE_3 = "other message 3";
    private static final String OTHER_MESSAGE_4 = "other message 4";
    private static final String OTHER_MESSAGE_5 = "other message 5";
    private static final String OTHER_MESSAGE_6 = "other message 6";
    private int count;
    private List<SmsRecord> records;

    public SmsRecordsBuilder() {
        this.records = buildRecords();
        this.count = this.records.size();
    }

    @Override
    public SmsRecords build() {
        SmsRecords smsRecords = new SmsRecords();
        smsRecords.setCount(this.count);
        smsRecords.setRecords(this.records);
        return smsRecords;
    }

    @Override
    public SmsRecords buildAsNew() {
        return build();
    }

    private List<SmsRecord> buildRecords() {
        List<SmsRecord> result = new ArrayList<>();
        result.add(new SmsRecordBuilder().build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_2)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_3)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_4)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_5)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_6)
                .build());
        return result;
    }
}
