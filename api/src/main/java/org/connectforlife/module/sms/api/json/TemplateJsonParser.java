/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Template;

import java.util.List;

/**
 * This class parses Json into Template objects and invokes suitable methods from <code>TemplateService</code>.
 *
 * @see TemplateService
 */
public class TemplateJsonParser {

    private TemplateService templateService;

    /**
     * Imports multiple templates from the given JSON. The imported templates are saved using the {@link TemplateService}.
     *
     * @param jsonText the JSON to parse for templates as a string - should contain an array of templates
     */
    public void importTemplates(String jsonText) {
        Gson gson = new Gson();
        List<Template> templateList = gson.fromJson(jsonText, new TypeToken<List<Template>>() {
        } .getType());
        templateService.importTemplates(templateList);
    }

    /**
     * Imports a single template from the given JSON. The imported template will be saved using the {@link TemplateService}.
     *
     * @param jsonText the JSON to parse for templates as a string - should contain a single template
     */
    public void importTemplate(String jsonText) {
        Gson gson = new Gson();
        Template template = gson.fromJson(jsonText, new TypeToken<Template>() {
        } .getType());
        templateService.importTemplate(template);
    }

    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }
}
