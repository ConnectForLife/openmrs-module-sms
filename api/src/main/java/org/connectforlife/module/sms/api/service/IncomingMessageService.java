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

import org.connectforlife.module.sms.api.handler.IncomingMessageHandler;
import org.openmrs.module.DaemonToken;
import org.connectforlife.module.sms.api.handler.IncomingMessageData;

/**
 * The IncomingMessageService Interface.
 *
 * <p>The IncomingMessage service is responsible for facilitating of incoming message processing in
 * the system.
 */
public interface IncomingMessageService {
  /**
   * Process an incoming message.
   *
   * <p>Execute processing for all configured {@link
   * IncomingMessageHandler}s. Any errors coming from handlers
   * are handled by this method.
   *
   * @param incomingMessage the message to handle, not null
   * @implNote Error in one handler, doesn't stop following handlers from executing.
   */
  void handleMessageSafe(IncomingMessageData incomingMessage);

  /**
   * Sets Daemon Token for this instance.
   *
   * @param token the token, not null
   */
  void setDaemonToken(DaemonToken token);
}
