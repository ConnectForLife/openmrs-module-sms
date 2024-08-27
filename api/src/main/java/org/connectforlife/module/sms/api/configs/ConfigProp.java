/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.configs;

/**
 * Provider-specific properties. ie: Clickatell api_key, most will have at least username and password
 */
public class ConfigProp {

    /**
     * The name of the property.
     */
    private String name;

    /**
     * The value of the property.
     */
    private String value;

    /**
     * Constructs a new prop without a name and a value.
     */
    public ConfigProp() {
    }

    /**
     * Constructs the property using the given name and value
     *
     * @param name  the name of the property
     * @param value the value of the property
     */
    public ConfigProp(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the property
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value of the property
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value of the property
     */
    public void setValue(String value) {
        this.value = value;
    }
}
