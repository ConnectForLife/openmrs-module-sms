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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.DaemonToken;
import org.connectforlife.module.sms.api.handler.IncomingMessageData;
import org.connectforlife.module.sms.api.service.IncomingMessageService;

/**
 * The base class for IncomingMessageService implementations, provides implementation of common
 * methods.
 */
public abstract class BaseIncomingMessageService implements IncomingMessageService {
  protected static final Log LOGGER = LogFactory.getLog(BaseIncomingMessageService.class);

  private DaemonToken daemonToken;

  @Override
  public void setDaemonToken(DaemonToken token) {
    this.daemonToken = token;
  }

  protected DaemonToken getDaemonToken() {
    return daemonToken;
  }

  protected boolean isTokenNotSet(IncomingMessageData incomingMessage) {
    if (daemonToken == null) {
      LOGGER.warn(
          "Received message before IncomingMessageService Service received Daemon Token (most likely before SMS "
              + "module startup). The incoming message is not handled (e.g.: automatic response won't be sent)! "
              + "Provider ID of the received message: "
              + incomingMessage.getProviderMessageId());
      return true;
    }

    return false;
  }
}
