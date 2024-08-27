/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.dao.impl;

import org.connectforlife.module.sms.builder.SmsRecordBuilder;
import org.connectforlife.module.sms.builder.SmsRecordSearchCriteriaBuilder;
import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.SmsRecordSearchCriteria;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SmsRecordDaoImplTest extends BaseModuleContextSensitiveTest {

    private static final String XML_DATA_SET_PATH = "datasets/";
    private static final int EXPECTED_ZERO = 0;
    private static final int EXPECTED_ONE = 1;
    private static final int EXPECTED_TWO = 2;
    private static final String FIRST_PROVIDER_ID = "f68cd3ff-3a83-4657-91e2-d240e5fd8ecf";
    private static final String FIRST_UUID = "16f280e0-3e91-48b4-acb2-c29c30595b45";
    private static final String SECOND_OPENMRS_ID = "ddffba98-aadb-4f37-90ed-b469b15515c1";
    private static final String SECOND_UUID = "e1c969a8-8c87-4058-ab2c-d3da042f4def";
    public static final String FIRST_OPENMRS_ID = "1b374ce7-d8f8-4ae5-b16c-78a35cb02ed2";
    public static final int PAGE = 1;
    public static final int PAGE_SIZE = 1;

    @Autowired
    @Qualifier("sms.SmsRecordDao")
    private SmsRecordDao smsRecordDao;

    @Before
    public void setUp() throws Exception {
        executeDataSet(XML_DATA_SET_PATH + "SmsRecordDataSet.xml");
    }

    @Test
    public void findByCriteria() {
        SmsRecordSearchCriteria criteria = prepareCriteria();
        List<SmsRecord> actual = smsRecordDao.findByCriteria(criteria);
        assertThat(actual.size(), is(EXPECTED_TWO));
    }

    @Test
    public void countFindByCriteria() {
        SmsRecordSearchCriteria criteria = prepareCriteria();
        long actual = smsRecordDao.countFindByCriteria(criteria);
        assertThat(actual, is((long) EXPECTED_TWO));
    }

    @Test
    public void findByProviderId() {
        List<SmsRecord> actual = smsRecordDao.findByProviderId(FIRST_PROVIDER_ID);
        assertThat(actual.size(), is(EXPECTED_ONE));
        assertThat(actual.get(0).getUuid(), is(FIRST_UUID));
    }

    @Test
    public void findByOpenMrsId() {
        List<SmsRecord> actual = smsRecordDao.findByOpenMrsId(SECOND_OPENMRS_ID);
        assertThat(actual.size(), is(EXPECTED_ONE));
        assertThat(actual.get(0).getUuid(), is(SECOND_UUID));
    }

    @Test
    public void findByProviderAndOpenMrsId() {
        List<SmsRecord> actual = smsRecordDao.findByProviderAndOpenMrsId(FIRST_PROVIDER_ID, FIRST_OPENMRS_ID);
        assertThat(actual.size(), is(EXPECTED_ONE));
        assertThat(actual.get(0).getUuid(), is(FIRST_UUID));
    }

    @Test
    public void deleteAll() {
        smsRecordDao.deleteAll();
        List<SmsRecord> actual = smsRecordDao.retrieveAll();
        assertThat(actual.size(), is(EXPECTED_ZERO));
    }

    @Test
    public void create() {
        SmsRecord expected = new SmsRecordBuilder().buildAsNew();
        smsRecordDao.createOrUpdate(expected);
        SmsRecord actual = smsRecordDao.getByUuid(expected.getUuid());
        assertSmsRecord(expected, actual);
    }

    @Test
    public void retrieveAll() {
        List<SmsRecord> actual = smsRecordDao.retrieveAll();
        assertThat(actual.size(), is(EXPECTED_ONE));
    }

    @Test
    public void findPageableByCriteria() {
        PagingInfo pagingInfo = new PagingInfo(PAGE, PAGE_SIZE);
        SmsRecordSearchCriteria criteria = prepareCriteria();
        List<SmsRecord> actual = smsRecordDao.findPageableByCriteria(pagingInfo, criteria);
        assertThat(actual.size(), is(EXPECTED_ONE));
    }

    private SmsRecordSearchCriteria prepareCriteria() {
        return new SmsRecordSearchCriteriaBuilder()
                .withConfig("nexmo")
                .withErrorMessage(null)
                .withInterval(null)
                .withMessageContent(null)
                .withOrder(null)
                .withPhoneNumber(null)
                .withProviderStatus(null)
                .build();
    }

    private void assertSmsRecord(SmsRecord expected, SmsRecord actual) {
        assertThat(actual.getConfig(), is(expected.getConfig()));
        assertThat(actual.getSmsDirection(), is(expected.getSmsDirection()));
        assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
        assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
        assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
        assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
        assertThat(actual.getOpenMrsId(), is(expected.getOpenMrsId()));
        assertThat(actual.getProviderId(), is(expected.getProviderId()));
        assertThat(actual.getErrorMessage(), is(expected.getErrorMessage()));
    }
}
