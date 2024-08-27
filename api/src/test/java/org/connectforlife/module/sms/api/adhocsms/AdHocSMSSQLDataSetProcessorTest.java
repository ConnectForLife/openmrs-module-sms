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

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.SQLQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class AdHocSMSSQLDataSetProcessorTest {

  private static final String QUERY = "SELECT '1122334455' AS Phone, \n" +
      "'sql msg3' AS 'SMS Text',\n" +
      "'{}' AS Parameters,\n" +
      "'17:16' As 'Contact time',\n" +
      "'nexmoWhatsapp' AS Config";

  private static final String DATASET_DEFINITION_UUID = "22008cd5-1315-11ed-b42a-0242ac160002";

  @Mock
  private DataSetDefinitionService dataSetDefinitionService;

  @Mock
  private DbSessionFactory dbSessionFactory;

  @Mock
  private DbSession dbSession;

  @Mock
  private SQLQuery sqlQuery;

  @InjectMocks
  private AdHocSMSSQLDataSetProcessor adHocSMSSQLDataSetProcessor;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getService(DataSetDefinitionService.class)).thenReturn(dataSetDefinitionService);
    when(dbSessionFactory.getCurrentSession()).thenReturn(dbSession);
  }

  @Test
  public void shouldGetAdHocSMSDataFromDataSetDefinition() {
    SqlDataSetDefinition testDataSet = buildTestSQLDataSetDefinition();
    when(dataSetDefinitionService.getDefinitionByUuid(DATASET_DEFINITION_UUID)).thenReturn(
        testDataSet);
    when(dbSession.createSQLQuery(testDataSet.getSqlQuery())).thenReturn(sqlQuery);

    adHocSMSSQLDataSetProcessor.getAdHocSMSData(buildInputSourceProcessorContext());

    verify(dataSetDefinitionService).getDefinitionByUuid(DATASET_DEFINITION_UUID);
    verify(dbSessionFactory).getCurrentSession();
    verify(dbSession).createSQLQuery(QUERY);
    verify(sqlQuery).list();
  }

  private AdHocSMSInputSourceProcessorContext buildInputSourceProcessorContext() {
    Map<String, String> contextOptionsMap = new HashMap<>();
    contextOptionsMap.put("dataSetUuid", DATASET_DEFINITION_UUID);
    return new AdHocSMSInputSourceProcessorContext(null, contextOptionsMap);
  }

  private SqlDataSetDefinition buildTestSQLDataSetDefinition() {
    SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
    dataSetDefinition.setSqlQuery(QUERY);
    dataSetDefinition.setUuid(DATASET_DEFINITION_UUID);
    return dataSetDefinition;
  }
}
