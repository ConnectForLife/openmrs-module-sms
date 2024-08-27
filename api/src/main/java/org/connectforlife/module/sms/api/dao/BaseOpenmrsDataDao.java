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

import org.connectforlife.module.sms.domain.PagingInfo;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;

public abstract class BaseOpenmrsDataDao<T extends BaseOpenmrsData> extends HibernateOpenmrsDataDAO<T> {

    private DbSessionFactory dbSessionFactory;

    protected BaseOpenmrsDataDao(Class<T> mappedClass) {
        super(mappedClass);
    }

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }

    protected DbSession getSession() {
        return dbSessionFactory.getCurrentSession();
    }

    /**
     * Loads the record count for the specified criteria into the specified paging object.
     *
     * @param pagingInfo The {@link PagingInfo} object to load with the record count.
     * @param criteria   The {@link Criteria} to execute against the hibernate data source or {@code null} to create a new one.
     */
    protected void loadPagingTotal(PagingInfo pagingInfo, Criteria criteria) {
        if (pagingInfo != null && pagingInfo.getPage() > 0 && pagingInfo.getPageSize() > 0) {
            Criteria paginationCriteria = criteria;
            if (criteria == null) {
                paginationCriteria = getSession().createCriteria(this.mappedClass);
            }

            if (pagingInfo.shouldLoadRecordCount()) {
                Long count = countRows(paginationCriteria);
                pagingInfo.setTotalRecordCount(count == null ? 0 : count);
                pagingInfo.setLoadRecordCount(false);
            }

        }
    }

    /**
     * Updates the specified {@link Criteria} object to retrieve the data specified by the {@link PagingInfo} object.
     *
     * @param pagingInfo The {@link PagingInfo} object that specifies which data should be retrieved.
     * @param criteria   The {@link Criteria} to add the paging settings to, or {@code null} to create a new one.
     * @return The {@link Criteria} object with the paging settings applied.
     */
    protected Criteria createPagingCriteria(PagingInfo pagingInfo, Criteria criteria) {
        Criteria paginationCriteria = criteria;
        if (pagingInfo != null && pagingInfo.getPage() > 0 && pagingInfo.getPageSize() > 0) {
            if (criteria == null) {
                paginationCriteria = getSession().createCriteria(this.mappedClass);
            }

            paginationCriteria.setFirstResult((pagingInfo.getPage() - 1) * pagingInfo.getPageSize());
            paginationCriteria.setMaxResults(pagingInfo.getPageSize());
            paginationCriteria.setFetchSize(pagingInfo.getPageSize());
        }

        return paginationCriteria;
    }

    /**
     * Count amount of rows for the specified criteria.
     *
     * @param criteria The {@link Criteria} tto execute against the hibernate data source
     * @return The row count
     */
    private Long countRows(Criteria criteria) {
        Long rows = (Long) criteria
                .setProjection(Projections.rowCount())
                .uniqueResult();
        // resetting criteria
        criteria.setProjection(null)
                .setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        return rows;
    }
}
