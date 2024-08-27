/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.handler;

/**
 * The IncomingMessageHandler Interface.
 *
 * <p>The implementation of this interface handles messages received by the system. The system
 * collects all beans which implement the IncomingMessageHandler, sorts them according to their
 * priorities and executes 'handle' methods for each in order - the execution 'chain' may be stopped
 * if proceeding handler's 'handle' method returns 'true' - that denotes a message to be handled,
 * and prevents any following handler from receiving the message.
 *
 * <p>The go-to use case for IncomingMessageHandler is sending an automatic response.
 *
 * @implSpec Each implementation must be a Spring bean.
 */
public interface IncomingMessageHandler {
  int BUILD_IN_PRIORITY = 10;

  /**
   * Used to determine an order of execution when multiple beans implementing the
   * IncomingMessageHandler are found.
   *
   * <p>The beans are sorted <b>from high to low value</b>.
   *
   * @return the priority of this specific implementation
   * @implNote The priority of a build-in implementation is defined by {@link #BUILD_IN_PRIORITY},
   *     implementor are free to reference it to position their beans.
   */
  int priority();

  /**
   * Handle the received message.
   *
   * @return true, if no further Message Handler should be called to process this specific message,
   *     false otherwise
   * @throws org.openmrs.api.APIException if the handling of message failed
   * @implSpec All exceptions must be wrapped in APIException. The implementor is responsible for
   *     creating a new transaction, or making sure that failure in this method call won't break the
   *     caller's transaction.
   */
  boolean handle(IncomingMessageData message);
}
