/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.service;

import org.connectforlife.module.sms.api.data.AutomaticResponseData;
import org.connectforlife.module.sms.api.util.AutomaticResponseEvaluationMessageContext;

import java.util.Optional;

/**
 * The AutomaticResponseEvaluatorService Class.
 *
 * <p>This service is responsible for evaluation what automatic message should be send back to a
 * sender of a message received by system.
 */
public interface AutomaticResponseEvaluatorService {
  Optional<AutomaticResponseData> evaluate(AutomaticResponseEvaluationMessageContext messageContext);
}
