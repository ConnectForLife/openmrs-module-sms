/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.web.controller.it;

import org.connectforlife.module.sms.api.audit.SmsAuditService;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.junit.After;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Verify SmsAuditService present & functional.
 */
public class SmsAuditServiceBundleITTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    @Qualifier("sms.SmsRecordDao")
    private SmsRecordDao smsRecordDao;

    @Autowired
    @Qualifier("sms.SmsAuditService")
    private SmsAuditService smsAuditService;

    @After
    public void cleanUpDatabase() throws Exception {
        this.deleteAllData();
    }

    @Test
    public void verifyServiceFunctional() {
        SmsRecord
            smsRecord = new SmsRecord("config", SmsDirection.INBOUND, "from", "message", DateUtil.now(),
                "PENDING", "status", "mid", "pid", null);
        smsRecordDao.createOrUpdate(smsRecord);

        List<SmsRecord> smsRecords = smsAuditService.findAllSmsRecords();
        assertEquals(1, smsRecords.size());
        assertEquals(smsRecords.get(0), smsRecord);
    }
}
