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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/** SMS record collection for the logging UI */
public class SmsLoggingRecords implements Serializable {

    private static final long serialVersionUID = 6510594252135942241L;

    /** The page number. */
    private final Integer page;

    /** The number of rows per page. */
    private final Integer total;

    /** The total number of records. */
    private final Long records;

    /** The data to display in the grid. */
    private final List<SmsLoggingRecord> rows;

    /**
     * Constructs an sms logging view for the jq grid.
     *
     * @param page         the page number
     * @param rows         the number of rows per page
     * @param totalRecords the total number of records
     * @param smsRecords   the data to display in the grid
     */
    public SmsLoggingRecords(Integer page, Integer rows, Long totalRecords, SmsRecords smsRecords) {
        this.page = page;
        records = totalRecords;
        total = rows;

        List<SmsLoggingRecord> smsLoggingRecords = new LinkedList<>();
        for (SmsRecord smsRecord : smsRecords.getRecords()) {
            smsLoggingRecords.add(new SmsLoggingRecord(smsRecord));
        }

        this.rows = smsLoggingRecords;
    }

    /** @return the page number */
    public Integer getPage() {
        return page;
    }

    /** @return the number of rows per page */
    public Integer getTotal() {
        return total;
    }

    /** @return the total number of records */
    public Long getRecords() {
        return records;
    }

    /** @return the data display in the grid */
    public List<SmsLoggingRecord> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return String.format("SmsLoggingRecords{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}
