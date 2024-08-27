/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.web.dto;

import org.connectforlife.module.sms.builder.SmsRecordsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.SmsRecords;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class SmsRecordsPageableTest {

    public static final int PAGE = 1;
    public static final int PAGE_SIZE = 231;

    @Test
    public void convertSmsRecordsToDTO() throws Exception {
        mockStatic(Context.class);
        AdministrationService administrationService = mock(AdministrationService.class);
        doReturn("").when(administrationService).getGlobalProperty(anyObject());
        doReturn(administrationService).when(Context.class, "getAdministrationService");
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPage(PAGE);
        pagingInfo.setPageSize(PAGE_SIZE);
        SmsRecords smsRecords = new SmsRecordsBuilder().build();
        SmsRecordsPageable smsRecordsPageable = new SmsRecordsPageable(pagingInfo, smsRecords);
        assertThat(smsRecordsPageable.getPageIndex(), is(PAGE));
        assertThat(smsRecordsPageable.getPageSize(), is(PAGE_SIZE));
        assertThat(smsRecordsPageable.getTotalRecords(), is(smsRecords.getCount()));
        assertDTOs(smsRecordsPageable.getRows(), smsRecords.getRecords());
    }

    @Test
    public void shouldWorkWithEmptyRecords() {
        SmsRecordsPageable smsRecordsPageable = new SmsRecordsPageable();
        smsRecordsPageable.setPageIndex(PAGE);
        smsRecordsPageable.setPageSize(PAGE_SIZE);
        smsRecordsPageable.setRows(new ArrayList<>());
        smsRecordsPageable.setTotalRecords(0);
        assertThat(smsRecordsPageable.getPageIndex(), is(PAGE));
        assertThat(smsRecordsPageable.getPageSize(), is(PAGE_SIZE));
        assertThat(smsRecordsPageable.getTotalRecords(), is(0));
        assertThat(smsRecordsPageable.getRows(), is(empty()));
    }

    private void assertDTOs(List<SmsRecordDTO> rows, List<SmsRecord> records) {
        for (int i = 0; i < records.size(); i++) {
            SmsRecordDTO actual = rows.get(i);
            SmsRecordDTO expected = toDTO(records.get(i));
            assertThat(actual.getId(), is(expected.getId()));
            assertThat(actual.getErrorMessage(), is(expected.getErrorMessage()));
            assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
            assertThat(actual.getOpenMrsId(), is(expected.getOpenMrsId()));
            assertThat(actual.getProviderId(), is(expected.getProviderId()));
            assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
            assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
            assertThat(actual.getTimestamp(), is(expected.getTimestamp()));
            assertThat(actual.getConfig(), is(expected.getConfig()));
            assertThat(actual.getSmsDirection(), is(expected.getSmsDirection()));
            assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
        }
    }

    private SmsRecordDTO toDTO(SmsRecord smsRecord) {
        SmsRecordDTO dto = new SmsRecordDTO();
        dto.setId(smsRecord.getId());
        dto.setErrorMessage(smsRecord.getErrorMessage());
        dto.setProviderStatus(smsRecord.getProviderStatus());
        dto.setOpenMrsId(smsRecord.getOpenMrsId());
        dto.setProviderId(smsRecord.getProviderId());
        dto.setDeliveryStatus(smsRecord.getDeliveryStatus());
        dto.setMessageContent(smsRecord.getMessageContent());
        dto.setTimestamp(DateUtil.getDateWithLocalTimeZone(smsRecord.getTimestamp()));
        dto.setConfig(smsRecord.getConfig());
        dto.setSmsDirection(smsRecord.getSmsDirection().name());
        dto.setPhoneNumber(smsRecord.getPhoneNumber());
        return dto;
    }
}
