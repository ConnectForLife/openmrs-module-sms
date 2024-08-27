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

import static java.util.stream.Collectors.joining;

import java.util.List;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseActionListener extends BaseListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseActionListener.class);

  /**
   * Performs action on specific event.
   *
   * @param message - processed message with provided properties
   */
  public abstract void performAction(Message message);

  /**
   * Handles messages received by this listener
   *
   * @param message - processed message with provided properties
   */
  @Override
  public void onMessage(final Message message) {
    LOGGER.trace("Handle onMessage");
    Daemon.runInDaemonThread(() -> performAction(message), getDaemonToken());
  }

  @Override
  public void subscribeSelf() {
    for (String action : subscribeToActions()) {
      for (Class<? extends OpenmrsObject> clazz : subscribeToObjects()) {
        Event.subscribe(clazz, action, this);
      }
    }
  }

  @Override
  public void unsubscribeSelf() {
    for (String action : subscribeToActions()) {
      for (Class<? extends OpenmrsObject> clazz : subscribeToObjects()) {
        Event.unsubscribe(clazz, Event.Action.valueOf(action), this);
      }
    }
  }

  @Override
  public String getSubscriptionDescription() {
    return "CRUD actions: " + String.join(",", subscribeToActions()) + "; Objects: " +
        subscribeToObjects().stream().map(Class::getName).collect(joining(","));
  }

  /**
   * Defines the list of classes which will be handled {@link #performAction(Message)} by this
   * listener
   *
   * @return a list of classes that this can handle
   */
  protected abstract List<Class<? extends OpenmrsObject>> subscribeToObjects();

  /**
   * Defines the list of Actions which will be performed {@link #performAction(Message)} by this
   * listener
   *
   * @return a list of supported Actions by this listener
   */
  protected abstract List<String> subscribeToActions();

  /**
   * Extracts an Action which caused the {@code message}.
   *
   * @param message the Event message, not null
   * @return the Action which cause the Event, never null
   * @throws IllegalArgumentException if {@code message} doesn't contain action or the value is
   *                                  wrong
   */
  protected Event.Action extractAction(Message message) {
    final String actionName = getMessagePropertyValue(message, "action");
    try {
      return Event.Action.valueOf(actionName);
    } catch (IllegalArgumentException eae) {
      throw new SmsRuntimeException("Unable to retrieve event action for name: " + actionName, eae);
    }
  }

  /**
   * Extracts a value of {@code propertyName} property from the {@code message}.
   *
   * @param message      the Event message, not null
   * @param propertyName the property name to extract, not null
   * @return the property value or null if there is no such property
   */
  protected String getMessagePropertyValue(Message message, String propertyName) {
    LOGGER.debug("Handle getMessagePropertyValue for '{}' property", propertyName);
    try {
      validateMessage(message);
      return ((MapMessage) message).getString(propertyName);
    } catch (JMSException e) {
      throw new SmsRuntimeException(
          "Exception while get uuid of created object from JMS message. " + e, e);
    }
  }

  private void validateMessage(Message message) {
    if (!(message instanceof MapMessage)) {
      throw new SmsRuntimeException("Event message has to be MapMessage");
    }
  }
}
