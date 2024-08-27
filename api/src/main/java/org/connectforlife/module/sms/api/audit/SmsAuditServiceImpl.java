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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.templates.Status;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.hibernate.criterion.Order;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.UserDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;


/**
 * Service that does the reading and writing to the SMS audit log.
 */
public class SmsAuditServiceImpl extends BaseOpenmrsService implements SmsAuditService {

    private static final Log LOGGER = LogFactory.getLog(SmsAuditServiceImpl.class);

    private static final String USER_DAO_BEAN_NAME = "userDAO";

    private static final String ADMIN_USER = "admin";

    @Override
    @Transactional
    public List<SmsRecord> findAllSmsRecords() {
        return getSmsRecordDao().retrieveAll();
    }

    @Override
    @Transactional
    public SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria) {
        List<SmsRecord> recordList = getSmsRecordDao().findByCriteria(criteria);

        return new SmsRecords(recordList.size(), recordList);
    }

    @Override
    @Transactional
    public long countAllSmsRecords(SmsRecordSearchCriteria criteria) {
        return getSmsRecordDao().countFindByCriteria(criteria);
    }

    @Override
    public SmsRecords findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria criteria) {
        List<SmsRecord> result = getSmsRecordDao().findPageableByCriteria(pagingInfo, criteria);
        int size = pagingInfo.shouldLoadRecordCount() ? result.size() : pagingInfo.getTotalRecordCount().intValue();
        return new SmsRecords(size, result);
    }

    @Override
    @Transactional
    public SmsRecord createOrUpdate(Status status, String configName, Map<String, String> params) {
        String statusString = params.get(status.getStatusKey());
        String providerMessageId = params.get(status.getMessageIdKey());
        SmsRecords smsRecords = findMatchingSmsRecords(configName, providerMessageId);
        SmsRecord existingRecord = findFirstByProviderMessageId(smsRecords, providerMessageId);

        if (existingRecord != null && !isProviderStatusAlreadyExists(smsRecords, statusString)) {
            SmsRecord smsRecord = createSmsRecordBasedOnExistingRecord(existingRecord, statusString, providerMessageId);
            setCreatorIfNeeded(smsRecord);
            return getSmsRecordDao().createOrUpdate(smsRecord);
        } else {
            String msg = String.format("SMS record with providerMessageId = %s not found", providerMessageId);
            LOGGER.error(msg);
            return null;
        }
    }

    @Override
    @Transactional
    public SmsRecord createOrUpdate(SmsRecord smsRecord) {
        SmsRecords smsRecords = findMatchingSmsRecords(smsRecord.getConfig(), smsRecord.getProviderId());
        if (CollectionUtils.isEmpty(smsRecords.getRecords()) ||
                !isProviderStatusAlreadyExists(smsRecords, smsRecord.getProviderStatus())) {
            setCreatorIfNeeded(smsRecord);
            return getSmsRecordDao().createOrUpdate(smsRecord);
        } else {
            String msg = String.format("SMS record with providerMessageId = %s not found", smsRecord.getProviderId());
            LOGGER.error(msg);
            return null;
        }
    }

    private boolean isProviderStatusAlreadyExists(SmsRecords smsRecords, String providerStatus) {
        for (SmsRecord smsRecord : smsRecords.getRecords()) {
            if (StringUtils.equalsIgnoreCase(smsRecord.getProviderStatus(), providerStatus)) {
                return true;
            }
        }
        return false;
    }

    private SmsRecord createSmsRecordBasedOnExistingRecord(SmsRecord existingSmsRecord, String statusString,
                                                           String providerMessageId) {
        return new SmsRecord(existingSmsRecord.getConfig(), existingSmsRecord.getSmsDirection(),
                existingSmsRecord.getPhoneNumber(), existingSmsRecord.getMessageContent(), DateUtil.now(),
                existingSmsRecord.getDeliveryStatus(), statusString, existingSmsRecord.getOpenMrsId(),
                providerMessageId, null);
    }

    private SmsRecords findMatchingSmsRecords(String configName, String providerMessageId) {
        Order order = Order.desc("timestamp");
        return findAllSmsRecords(new SmsRecordSearchCriteria()
                .withConfig(configName)
                .withProviderId(providerMessageId)
                .withOrder(order));
    }

    private SmsRecord findFirstByProviderMessageId(SmsRecords smsRecords, String providerMessageId) {
        for (SmsRecord smsRecord : smsRecords.getRecords()) {
            if (smsRecord.getProviderId().equals(providerMessageId)) {
                return smsRecord;
            }
        }
        return null;
    }

    private void setCreatorIfNeeded(SmsRecord smsRecord) {
        if (Context.isSessionOpen() && !Context.isAuthenticated()) {
            smsRecord.setCreator(Context.getRegisteredComponent(USER_DAO_BEAN_NAME, UserDAO.class)
                    .getUserByUsername(ADMIN_USER));
        }
    }

    private SmsRecordDao getSmsRecordDao() {
        return Context.getRegisteredComponent("sms.SmsRecordDao", SmsRecordDao.class);
    }
}
