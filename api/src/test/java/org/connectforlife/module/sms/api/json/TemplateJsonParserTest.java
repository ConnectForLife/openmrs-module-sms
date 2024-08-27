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

import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.api.service.SettingsManagerService;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.util.ResourceUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TemplateJsonParserTest extends BaseModuleContextSensitiveTest {

    private static final String SMS_TEMPLATES_JSON = "sms-templates.json";
    private static final String SMS_TEMPLATE_JSON = "templates/provider-status-template.json";

    @Autowired
    @Qualifier("sms.templateJsonParser")
    private TemplateJsonParser templateJsonParser;

    @Autowired
    @Qualifier("sms.settings.manager")
    private SettingsManagerService settingsManagerService;

    @Autowired
    @Qualifier("templateService")
    private TemplateService templateService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(templateService, "templates", new HashMap<>());
    }

    @Test
    public void importTemplates() {
        String templateName = "nexmo";
        assertThatMissing(templateName);
        String jsonText = ResourceUtil.readResourceFile(SMS_TEMPLATES_JSON);
        templateJsonParser.importTemplates(jsonText);
        Template template = templateService.getTemplate(templateName);
        assertThat(template, is(notNullValue()));
        assertThat(template.toString(), is(notNullValue()));
        assertThat(template.getName(), is(templateName));
    }

    @Test
    public void importTemplate() {
        String templateName = "nexmo";
        assertThatMissing(templateName);
        String jsonText = ResourceUtil.readResourceFile(SMS_TEMPLATE_JSON);
        templateJsonParser.importTemplate(jsonText);
        Template template = templateService.getTemplate(templateName);
        assertThat(template, is(notNullValue()));
        assertThat(template.getName(), is(templateName));
    }

    private void assertThatMissing(String configName) {
        boolean thrown = false;
        try {
            templateService.getTemplate(configName);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}
