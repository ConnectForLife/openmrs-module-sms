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

import org.connectforlife.module.sms.api.templates.Status;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.connectforlife.module.sms.api.util.PrivilegeConstants;

import java.util.List;
import java.util.Map;

/**
 * Reading and writing to the SMS audit log
 */
public interface SmsAuditService extends OpenmrsService {

    /**
     * Finds and returns all <code>SmsRecord</code> entries in the sms log.
     *
     * @return all sms records in the sms log
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    List<SmsRecord> findAllSmsRecords();

    /**
     * Finds and returns all <code>SmsRecords</code> entries matching the specified
     * search criteria.
     *
     * @return all sms records matching the provided criteria
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria);

    /**
     * Returns the count of <code>SmsRecords</code> entries matching the specified
     * search criteria.
     *
     * @return the count of sms records matching the provided criteria
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    long countAllSmsRecords(SmsRecordSearchCriteria criteria);

    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    SmsRecords findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria criteria);

    SmsRecord createOrUpdate(Status status, String configName, Map<String, String> params);

    SmsRecord createOrUpdate(SmsRecord smsRecord);

}
