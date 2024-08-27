/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.adhocsms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.connectforlife.module.sms.api.data.AdHocSMSData;
import org.springframework.transaction.annotation.Transactional;

public class AdHocSMSSQLDataSetProcessor implements AdHocSMSInputSourceProcessor {

  private static final Log LOGGER = LogFactory.getLog(AdHocSMSSQLDataSetProcessor.class);

  public static final String DATA_SET_UUID_PROP_NAME = "dataSetUuid";

  private DbSessionFactory dbSessionFactory;

  public AdHocSMSSQLDataSetProcessor(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  @Override
  public boolean shouldProcessData(AdHocSMSInputSourceProcessorContext context) {
    return context.getOptions().containsKey(DATA_SET_UUID_PROP_NAME);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context) {
    String dataSetUuid = context.getOptions().get(DATA_SET_UUID_PROP_NAME);
    DataSetDefinition dataSet = Context.getService(DataSetDefinitionService.class)
        .getDefinitionByUuid(dataSetUuid);
    if (dataSet == null) {
      throw new EntityNotFoundException(
          String.format("Data set definition with uuid %s not found", dataSetUuid));
    }

    SQLQuery sqlQuery = getSQLQuery((SqlDataSetDefinition) dataSet);
    return getSMSData(sqlQuery.list());
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  private SQLQuery getSQLQuery(SqlDataSetDefinition sqlDataSetDefinition) {
    SQLQuery sqlQuery = dbSessionFactory.getCurrentSession()
        .createSQLQuery(sqlDataSetDefinition.getSqlQuery());
    sqlQuery.setResultTransformer((Transformers.ALIAS_TO_ENTITY_MAP));

    return sqlQuery;
  }

  private List<AdHocSMSData> getSMSData(List<Object> queryResults) {
    List<AdHocSMSData> resultList = new ArrayList<>();
    try {
      for (Object object : queryResults) {
        resultList.add(new AdHocSMSData((Map<String, String>) object));
      }
    } catch (IOException ex) {
      LOGGER.error("Error occurred while mapping query results into AdHocSMSData class", ex);
    }

    return resultList;
  }
}
