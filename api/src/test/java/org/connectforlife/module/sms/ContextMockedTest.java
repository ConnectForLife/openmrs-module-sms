/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.connectforlife.module.sms.api.service.AdHocSMSInputSourceProcessorService;
import org.connectforlife.module.sms.api.service.ScheduleAdHocSMSesService;
import org.openmrs.scheduler.SchedulerService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public abstract class ContextMockedTest {

  @Mock
  protected SchedulerService schedulerService;

  @Mock
  protected AdministrationService administrationService;

  @Mock
  protected AdHocSMSInputSourceProcessorService adHocSMSInputSourceProcessorService;

  @Mock
  protected ScheduleAdHocSMSesService scheduleAdHocSMSesService;

  @Mock
  protected DataSetDefinitionService dataSetDefinitionService;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getSchedulerService()).thenReturn(schedulerService);
    when(Context.getAdministrationService()).thenReturn(administrationService);
    when(Context.getService(AdHocSMSInputSourceProcessorService.class)).thenReturn(
        adHocSMSInputSourceProcessorService);
    when(Context.getService(ScheduleAdHocSMSesService.class)).thenReturn(scheduleAdHocSMSesService);
    when(Context.getService(DataSetDefinitionService.class)).thenReturn(dataSetDefinitionService);
  }
}
