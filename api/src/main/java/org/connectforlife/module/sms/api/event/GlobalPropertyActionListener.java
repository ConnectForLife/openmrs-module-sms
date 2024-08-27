/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.event;

import java.util.Collections;
import java.util.List;
import javax.jms.Message;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.GlobalProperty;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;

public abstract class GlobalPropertyActionListener extends BaseActionListener {

  @Override
  protected List<Class<? extends OpenmrsObject>> subscribeToObjects() {
    return Collections.singletonList(GlobalProperty.class);
  }

  protected GlobalProperty extractGlobalProperty(Message message) {
    String globalPropertyUuid = getMessagePropertyValue(message, "uuid");
    return getGlobalProperty(globalPropertyUuid);
  }

  private GlobalProperty getGlobalProperty(String globalPropertyUuid) {
    GlobalProperty globalProperty = Context.getAdministrationService()
        .getGlobalPropertyByUuid(globalPropertyUuid);
    if (globalProperty == null) {
      throw new SmsRuntimeException(String.format("Unable to retrieve global property by uuid: %s",
          globalPropertyUuid));
    }
    return globalProperty;
  }
}
