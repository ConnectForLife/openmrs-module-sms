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

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.connectforlife.module.sms.api.handler.IncomingMessageData;
import org.connectforlife.module.sms.api.handler.IncomingMessageHandler;
import org.connectforlife.module.sms.api.util.SMSConstants;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/** The default implementation of IncomingMessageService. */
public class IncomingMessageServiceImpl extends BaseIncomingMessageService {
  @Override
  public void handleMessageSafe(IncomingMessageData incomingMessage) {
    if (isTokenNotSet(incomingMessage) || isHandlersDisabled()) {
      return;
    }
    Daemon.runInDaemonThreadAndWait(
        () -> internalHandleMessage(incomingMessage), getDaemonToken());
  }

  private boolean isHandlersDisabled() {
    final String parameterValue =
        Context.getAdministrationService()
            .getGlobalProperty(SMSConstants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS);
    return Boolean.parseBoolean(parameterValue);
  }

  private void internalHandleMessage(IncomingMessageData incomingMessage) {
    final Iterator<IncomingMessageHandler> handlersChain = getHandlers();

    while (handlersChain.hasNext()) {
      final IncomingMessageHandler currentHandler = handlersChain.next();
      handle(incomingMessage, currentHandler);
    }
  }

  private Iterator<IncomingMessageHandler> getHandlers() {
    final List<IncomingMessageHandler> allHandlers =
        Context.getRegisteredComponents(IncomingMessageHandler.class);
    allHandlers.sort(Comparator.comparing(IncomingMessageHandler::priority).reversed());
    return allHandlers.iterator();
  }

  private void handle(IncomingMessageData incomingMessage, IncomingMessageHandler handler) {
    try {
      handler.handle(incomingMessage);
    }catch (APIException apiException) {
      LOGGER.error(
         MessageFormat.format(
              "Error executing Incoming Message handler: {0}. Cause: {1}",
              handler.getClass().getName(), apiException.toString()),
          apiException);
    }
  }
}
