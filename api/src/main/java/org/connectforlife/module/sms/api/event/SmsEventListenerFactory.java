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

import org.openmrs.api.context.Context;
import org.openmrs.event.Event;

import java.util.List;

public final class SmsEventListenerFactory {

  public static void registerEventListeners() {
    List<AbstractSmsEventListener> eventComponents = Context.getRegisteredComponents(
        AbstractSmsEventListener.class);
    for (AbstractSmsEventListener eventListener : eventComponents) {
      subscribeListener(eventListener);
    }

    final List<BaseListener> listeners = Context.getRegisteredComponents(BaseListener.class);
    for (BaseListener listener : listeners) {
      listener.subscribeSelf();
    }
  }

  public static void unRegisterEventListeners() {
    List<AbstractSmsEventListener> eventComponents = Context.getRegisteredComponents(
        AbstractSmsEventListener.class);
    for (AbstractSmsEventListener eventListener : eventComponents) {
      unSubscribeListener(eventListener);
    }

    final List<BaseListener> listeners = Context.getRegisteredComponents(BaseListener.class);
    for (BaseListener listener : listeners) {
      listener.unsubscribeSelf();
    }
  }

  private static void subscribeListener(AbstractSmsEventListener smsEventListener) {
    for (String subject : smsEventListener.getSubjects()) {
      Event.subscribe(subject, smsEventListener);
    }
  }

  private static void unSubscribeListener(AbstractSmsEventListener smsEventListener) {
    for (String subject : smsEventListener.getSubjects()) {
      Event.unsubscribe(subject, smsEventListener);
    }
  }

  private SmsEventListenerFactory() {
  }
}
