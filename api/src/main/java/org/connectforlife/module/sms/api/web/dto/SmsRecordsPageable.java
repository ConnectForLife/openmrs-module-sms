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

import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.SmsRecords;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.connectforlife.module.sms.api.web.dto.builder.SmsRecordDTOBuilder;

import java.util.ArrayList;
import java.util.List;

public class SmsRecordsPageable extends BaseDTO {

    private static final long serialVersionUID = -2734653856427001890L;

    private Integer pageIndex;

    private Integer pageSize;

    private Integer totalRecords;

    private List<SmsRecordDTO> rows;

    public SmsRecordsPageable() {
    }

    public SmsRecordsPageable(PagingInfo page, SmsRecords smsRecords) {
        this.pageIndex = page.getPage();
        this.pageSize = page.getPageSize();
        this.totalRecords = smsRecords.getCount();
        this.rows = convertSmsRecordsToDTO(smsRecords.getRecords());
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<SmsRecordDTO> getRows() {
        return rows;
    }

    public void setRows(List<SmsRecordDTO> rows) {
        this.rows = rows;
    }

    private List<SmsRecordDTO> convertSmsRecordsToDTO(List<SmsRecord> records) {
        List<SmsRecordDTO> result = new ArrayList<>(records.size());
        for (SmsRecord smsRecord : records) {
            result.add(new SmsRecordDTOBuilder()
                    .setId(smsRecord.getId())
                    .setErrorMessage(smsRecord.getErrorMessage())
                    .setProviderStatus(smsRecord.getProviderStatus())
                    .setOpenMrsId(smsRecord.getOpenMrsId())
                    .setProviderId(smsRecord.getProviderId())
                    .setDeliveryStatus(smsRecord.getDeliveryStatus())
                    .setMessageContent(smsRecord.getMessageContent())
                    .setTimestamp(DateUtil.getDateWithLocalTimeZone(smsRecord.getTimestamp()))
                    .setConfig(smsRecord.getConfig())
                    .setSmsDirection(smsRecord.getSmsDirection())
                    .setPhoneNumber(smsRecord.getPhoneNumber())
                    .setModificationDate(DateUtil.getDateWithLocalTimeZone(smsRecord.getDateChanged()))
                    .setCreationDate(DateUtil.getDateWithLocalTimeZone(smsRecord.getDateCreated()))
                    .setModifiedBy(smsRecord.getChangedBy())
                    .setCreator(smsRecord.getCreator())
                    .createSmsRecordDTO());
        }
        return result;
    }
}
