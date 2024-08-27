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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSmsEventListener implements EventListener {

    private static final Log LOGGER = LogFactory.getLog(AbstractSmsEventListener.class);

    private DaemonToken daemonToken;

    @Override
    public void onMessage(Message message) {
        try {
            Map<String, Object> properties = getProperties((MapMessage) message);
            Daemon.runInDaemonThread(() -> handleEvent(properties), daemonToken);
        } catch ( JMSException e ) {
            // generic error handling is used to avoid the ActiveMQ retrying mechanism (retry 6 times)
            LOGGER.error("Error during handling Sms event", e);
        }
    }

    public abstract String[] getSubjects();

    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
    }

    protected abstract void handleEvent(Map<String, Object> properties);

    protected <T> T getComponent(String beanName, Class<T> type) {
        return Context.getRegisteredComponent(beanName, type);
    }

    private Map<String, Object> getProperties(MapMessage mapMessage) throws JMSException {
        final Enumeration<String> propertiesKey = (Enumeration<String>) mapMessage.getMapNames();
        Map<String, Object> properties = new HashMap<>();

        while (propertiesKey.hasMoreElements()) {
            String key = propertiesKey.nextElement();
            properties.put(key, mapMessage.getObject(key));
        }
        return properties;
    }

}
