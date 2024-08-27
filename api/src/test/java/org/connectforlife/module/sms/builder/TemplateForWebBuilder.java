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

import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.templates.TemplateForWeb;

import java.util.ArrayList;
import java.util.List;

public class TemplateForWebBuilder extends AbstractBuilder<TemplateForWeb> {

    private String name;
    private List<String> configurables;

    public TemplateForWebBuilder() {
        this.name = "nexmo";
        this.configurables = buildConfigurables();
    }

    @Override
    public TemplateForWeb build() {
        TemplateForWeb templateForWeb = new TemplateForWeb(new Template());
        templateForWeb.setName(this.name);
        templateForWeb.setConfigurables(this.configurables);
        return templateForWeb;
    }

    @Override
    public TemplateForWeb buildAsNew() {
        return build();
    }

    public String getName() {
        return name;
    }

    public TemplateForWebBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getConfigurables() {
        return configurables;
    }

    public TemplateForWebBuilder withConfigurables(List<String> configurables) {
        this.configurables = configurables;
        return this;
    }

    private List<String> buildConfigurables() {
        List<String> result = new ArrayList<>();
        result.add("from");
        result.add("api_key");
        result.add("api_secret");
        return result;
    }
}
