/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.dao;

import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.SmsRecordSearchCriteria;
import org.connectforlife.module.sms.domain.PagingInfo;
import org.openmrs.api.db.OpenmrsDataDAO;

import java.util.List;

/**
 * Used to query and save audit records in the database.
 * This is a service interface for which MDS will generate the implementation at runtime.
 */
public interface SmsRecordDao extends OpenmrsDataDAO<SmsRecord> {

    /**
     * Retrieves all sms records matching the given criteria. All string fields in this lookup are matched
     * using the matches() operator, meaning they will be matched using the same rules that {@link String#matches(String)}
     * uses.
     *
     * @param searchCriteria the search criteria
     * @return the matching records
     */
    List<SmsRecord> findByCriteria(SmsRecordSearchCriteria searchCriteria);

    /**
     * Retrieves the total count of SMS messages matching the given criteria. All string fields in this lookup are matched
     * using the matches() operator, meaning they will be matched using the same rules that {@link String#matches(String)}
     * uses.
     *
     * @param searchCriteria the search criteria
     * @return the matching records
     */
    long countFindByCriteria(SmsRecordSearchCriteria searchCriteria);

    /**
     * Retrieves records by the provider ID.
     *
     * @param providerId the provider ID
     * @return the list of matching records
     */
    List<SmsRecord> findByProviderId(String providerId);

    /**
     * Retrieves records by the ID.
     *
     * @param openMrsId the OpenMRS ID
     * @return the list of matching records
     */
    List<SmsRecord> findByOpenMrsId(String openMrsId);

    /**
     * Retrieves records by both provider and OpenMRS IDs.
     *
     * @param providerId the provider ID
     * @param openMrsId  the OpenMRS ID
     * @return the list of matching records
     */
    List<SmsRecord> findByProviderAndOpenMrsId(String providerId, String openMrsId);

    void deleteAll();

    SmsRecord createOrUpdate(SmsRecord smsRecord);

    List<SmsRecord> retrieveAll();

    /**
     * Retrieves records by the specified criteria and based on the specified paging object.
     *
     * @param pagingInfo     The {@link PagingInfo} object contains pageable configuration.
     * @param searchCriteria The {@link SmsRecordSearchCriteria} object specifying condition for criteria
     * @return The list of matching records
     */
    List<SmsRecord> findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria searchCriteria);
}
