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

import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.templates.TemplateForWeb;

import java.util.List;
import java.util.Map;

/**
 * Template service, manages SMS Templates. A template represents the way all users connect to an SMS provider.
 * See {@link Template}
 */
public interface TemplateService {

    /**
     * Retrieves the template with the given name.
     *
     * @param name the name of the template
     * @return the template with the matching name, never null
     * @throws IllegalArgumentException if no such template exists
     */
    Template getTemplate(String name);

    /**
     * Retrieves all templates as objects for the UI display.
     *
     * @return map of the template representations, where keys are the names of the templates
     */
    Map<String, TemplateForWeb> allTemplatesForWeb();

    /**
     * Imports the custom provider templates provided by the user. These templates will
     * be added to the additional templates configuration.
     *
     * @param templateList the templates to import
     */
    void importTemplates(List<Template> templateList);

    /**
     * Imports the custom provider template provided by the user. This template will
     * be added to the additional templates configuration.
     *
     * @param template the template to import
     */
    void importTemplate(Template template);

    /**
     * Loads the default templates. The method should be run after the module is started.
     */
    void loadTemplates();

    /**
     * Gets all default provider templates.
     *
     * @return the list of default provider templates, never null
     */
    List<Template> getDefaultTemplates();

    /**
     * Gets all custom provider templates.
     *
     * @return the list of custom provider templates, never null
     */
    List<Template> getCustomTemplates();

    /**
     * Gets all provider templates.
     *
     * @return the list of provider templates, never null
     */
    List<Template> getAllTemplates();

    /**
     * Writes {@code defaultTemplates} into the file.
     * <p>
     *     This method only writes templates, if you want them usable you need to call {@link #loadTemplates()}
     * </p>
     *
     * @param defaultTemplates the templates to write, not null
     */
    void writeDefaultTemplates(List<Template> defaultTemplates);

    /**
     * Writes {@code customTemplates} into the file.
     * <p>
     *     This method only writes templates, if you want them usable you need to call {@link #loadTemplates()}
     * </p>
     *
     * @param customTemplates the templates to write, not null
     */
    void writeCustomTemplates(List<Template> customTemplates);
}
