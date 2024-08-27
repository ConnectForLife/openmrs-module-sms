/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.builder;

import org.connectforlife.module.sms.api.configs.ConfigProp;

public class ConfigPropBuilder extends AbstractBuilder<ConfigProp> {

    private String name;
    private String value;

    public ConfigPropBuilder() {
        this.name = "from";
        this.value = "48777666222";
    }

    @Override
    public ConfigProp build() {
        ConfigProp configProp = new ConfigProp();
        configProp.setName(this.name);
        configProp.setValue(this.value);
        return configProp;
    }

    @Override
    public ConfigProp buildAsNew() {
        return build();
    }

    public String getName() {
        return name;
    }

    public ConfigPropBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ConfigPropBuilder withValue(String value) {
        this.value = value;
        return this;
    }
}
