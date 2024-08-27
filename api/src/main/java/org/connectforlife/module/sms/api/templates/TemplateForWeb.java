/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.templates;

import java.util.List;

/**
 * A simplified form of the template class used in the web settings UI
 */
public class TemplateForWeb {

    /**
     * The unique name of the template.
     */
    private String name;

    /**
     * The names of fields that are configurable for this template.
     */
    private List<String> configurables;

    /**
     * Constructs this DTO from the provided {@link Template} object.
     *
     * @param template the backing template
     */
    public TemplateForWeb(Template template) {
        this.name = template.getName();
        this.configurables = template.getConfigurables();
    }

    /**
     * @return the unique name of the template
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the unique name of the template
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the names of fields that are configurable for this template
     */
    public List<String> getConfigurables() {
        return configurables;
    }

    /**
     * @param configurables the names of fields that are configurable for this template
     */
    public void setConfigurables(List<String> configurables) {
        this.configurables = configurables;
    }
}
