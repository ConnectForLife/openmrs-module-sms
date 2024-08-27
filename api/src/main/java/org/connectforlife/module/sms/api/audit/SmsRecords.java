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
import java.util.ArrayList;
import java.util.List;

/** SMS audit records from the database. Represents a subset of all records. */
public class SmsRecords implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The total number of SMS records in the current context. */
  private int count = 0;

  /**
   * The records to display. The number of records should be less than or equal to the total record
   * count. SuppressWarnings because serializable type is needed.
   */
  @SuppressWarnings("PMD.LooseCoupling")
  private ArrayList<SmsRecord> records = new ArrayList<>();

  /** Constructs an instance with no records and a total count of zero. */
  public SmsRecords() {}

  /**
   * Constructs an instance from the given records and count.
   *
   * @param count the total count of records in this context
   * @param records the subset of records
   */
  public SmsRecords(int count, List<SmsRecord> records) {
    this.count = count;
    this.records.addAll(records);
  }

  /** @return the total number of SMS records in the current context */
  public int getCount() {
    return count;
  }

  /** @param count the total number of SMS records in the current context */
  public void setCount(int count) {
    this.count = count;
  }

  /** @return the actual subset of records */
  public List<SmsRecord> getRecords() {
    return records;
  }

  /** @param records the actual subset of records */
  public void setRecords(List<SmsRecord> records) {
    this.records.clear();
    this.records.addAll(records);
  }
}
