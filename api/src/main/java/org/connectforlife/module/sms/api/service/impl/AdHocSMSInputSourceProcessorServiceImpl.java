/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSInputSourceProcessor;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSInputSourceProcessorContext;
import org.connectforlife.module.sms.api.service.AdHocSMSInputSourceProcessorService;
import org.openmrs.api.context.Context;
import org.connectforlife.module.sms.api.data.AdHocSMSData;

public class AdHocSMSInputSourceProcessorServiceImpl implements
    AdHocSMSInputSourceProcessorService {

  @Override
  public List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context) {
    List<AdHocSMSData> result = new ArrayList<>();
    List<AdHocSMSInputSourceProcessor> inputSourceProcessors = Context.getRegisteredComponents(
        AdHocSMSInputSourceProcessor.class);
    for (AdHocSMSInputSourceProcessor processor : inputSourceProcessors) {
      if (processor.shouldProcessData(context)) {
        result = processor.getAdHocSMSData(context);
      }
    }
    return result;
  }
}
