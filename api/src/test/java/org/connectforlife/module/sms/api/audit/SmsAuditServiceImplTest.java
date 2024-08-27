/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.audit;

import org.connectforlife.module.sms.builder.SmsRecordSearchCriteriaBuilder;
import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.templates.Status;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SmsAuditServiceImplTest extends BaseModuleContextSensitiveTest {

  private static final String XML_DATA_SET_PATH = "datasets/";
  private static final int EXPECTED_ONE = 1;
  private static final int EXPECTED_TWO = 2;
  private static final String CONFIG = "nexmo";
  private static final String MessageIdKey = "MESSAGE_ID_KEY";
  private static final String StatusIdKey = "STATUS_ID_KEY";
  private static final String FIRST_UUID = "16f280e0-3e91-48b4-acb2-c29c30595b45";
  private static final String SECOND_UUID = "e1c969a8-8c87-4058-ab2c-d3da042f4def";
  private static final String BAD_CREDENTIALS = "Bad Credentials";
  public static final int PAGE = 1;
  public static final int PAGE_SIZE = 1;

  @Autowired
  @Qualifier("sms.SmsAuditService")
  private SmsAuditService smsAuditService;

  @Before
  public void setUp() throws Exception {
    executeDataSet(XML_DATA_SET_PATH + "SmsRecordDataSet.xml");
  }

  @Test
  public void findAllSmsRecordsShouldReturnExpectedResults() {
    List<SmsRecord> actual = smsAuditService.findAllSmsRecords();
    assertThat(actual.size(), is(EXPECTED_ONE));
    assertThat(actual.get(0).getUuid(), is(FIRST_UUID));
  }

  @Test
  public void findAllSmsRecordsByCriteriaShouldReturnExpectedResults() {
    SmsRecordSearchCriteria criteria = prepareCriteria();
    SmsRecords actual = smsAuditService.findAllSmsRecords(criteria);
    assertThat(actual.getCount(), is(EXPECTED_TWO));
    assertThat(actual.getRecords().get(0).getUuid(), is(FIRST_UUID));
    assertThat(actual.getRecords().get(1).getUuid(), is(SECOND_UUID));
  }

  @Test
  public void countAllSmsRecordsShouldReturnExpectedResult() {
    SmsRecordSearchCriteria criteria = prepareCriteria();
    long actual = smsAuditService.countAllSmsRecords(criteria);
    assertThat(actual, is((long) EXPECTED_TWO));
  }

  @Test
  public void findPageableByCriteria() {
    PagingInfo pagingInfo = new PagingInfo(PAGE, PAGE_SIZE);
    SmsRecordSearchCriteria criteria = prepareCriteria();
    SmsRecords actual = smsAuditService.findPageableByCriteria(pagingInfo, criteria);
    assertThat(actual.getRecords().size(), is(EXPECTED_ONE));
    assertThat(actual.getRecords().get(0).getUuid(), is(FIRST_UUID));
    assertThat(actual.getCount(), is(EXPECTED_TWO));
  }

  @Test
  public void withSmsRecordCreateOrUpdate() {
    SmsRecord smsRecord = new SmsRecord();
    smsRecord.setConfig(CONFIG);
    smsRecord.setErrorMessage(BAD_CREDENTIALS);
    smsRecord.setDeliveryStatus(DeliveryStatusesConstants.ABORTED);
    smsRecord.setProviderStatus("1");
    smsRecord.setDateCreated(DateUtil.now());
    smsRecord.setProviderId("e1e7406b-4285-4b34-908a-771a4f2f8370");
    SmsRecord actual = smsAuditService.createOrUpdate(smsRecord);
    List<SmsRecord> expected = smsAuditService.findAllSmsRecords();
    assertThat(actual.getConfig(), is(expected.get(1).getConfig()));
    assertThat(actual.getDeliveryStatus(), is(expected.get(1).getDeliveryStatus()));
    assertThat(actual.getErrorMessage(), is(expected.get(1).getErrorMessage()));
    assertThat(actual.getProviderStatus(), is(expected.get(1).getProviderStatus()));
    assertThat(actual.getProviderId(), is(expected.get(1).getProviderId()));
    assertThat(actual.getDateCreated(), is(expected.get(1).getDateCreated()));
  }

  @Test
  public void withStatusConfigAndParamsCreateOrUpdate() {

    Status status = new Status();
    status.setMessageIdKey(MessageIdKey);
    status.setStatusKey(StatusIdKey);
    Map<String, String> PARAMS =
        new HashMap<String, String>() {
          {
            put(MessageIdKey, "e1e7406b-4285-4b34-908a-771a4f2f8370");
            put(StatusIdKey, "1");
          }
        };
    SmsRecord actual = smsAuditService.createOrUpdate(status, CONFIG, PARAMS);
    List<SmsRecord> expected = smsAuditService.findAllSmsRecords();
    assertThat(actual.getProviderId(), is(expected.get(1).getProviderId()));
  }

  @Test
  public void WithAlreadyExistsProviderStatusConfigAndParamsCreateOrUpdate() {

    Status status = new Status();
    status.setMessageIdKey(MessageIdKey);
    status.setStatusKey(StatusIdKey);
    Map<String, String> PARAMS =
        new HashMap<String, String>() {
          {
            put(MessageIdKey, "e1e7406b-4285-4b34-908a-771a4f2f8370");
            put(StatusIdKey, "0");
          }
        };
    SmsRecord actual = smsAuditService.createOrUpdate(status, CONFIG, PARAMS);
    assertNull(actual);
  }

  @Test
  public void WithAlreadyExistsProviderStatusSmsRecordCreateOrUpdate() {
    SmsRecord smsRecord = new SmsRecord();
    smsRecord.setConfig(CONFIG);
    smsRecord.setErrorMessage(BAD_CREDENTIALS);
    smsRecord.setDeliveryStatus(DeliveryStatusesConstants.ABORTED);
    smsRecord.setProviderStatus("0");
    smsRecord.setDateCreated(DateUtil.now());
    SmsRecord actual = smsAuditService.createOrUpdate(smsRecord);
    assertNull(actual);
  }

  @Test
  public void WithNewProviderIdSmsRecordCreateOrUpdate() {
    Status status = new Status();
    status.setMessageIdKey(MessageIdKey);
    status.setStatusKey(StatusIdKey);
    Map<String, String> PARAMS =
        new HashMap<String, String>() {
          {
            put(MessageIdKey, "e1e7406b");
            put(StatusIdKey, "0");
          }
        };
    SmsRecord actual = smsAuditService.createOrUpdate(status, CONFIG, PARAMS);
    assertNull(actual);
  }

  private SmsRecordSearchCriteria prepareCriteria() {
    return new SmsRecordSearchCriteriaBuilder()
        .withConfig(CONFIG)
        .withErrorMessage(null)
        .withInterval(null)
        .withMessageContent(null)
        .withOrder(null)
        .withPhoneNumber(null)
        .withProviderStatus(null)
        .build();
  }
}
