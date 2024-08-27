/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 *
 */

package org.connectforlife.module.sms.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.configs.ConfigProp;
import org.connectforlife.module.sms.api.service.ConfigService;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.templates.Template;

import java.util.HashMap;
import java.util.Map;

public class TemplatePropertiesBuilder {
  private static final Log LOGGER = LogFactory.getLog(TemplatePropertiesBuilder.class);

  private ConfigService configService;
  private OutgoingSms outgoingSms;
  private Template template;
  private Config config;

  public TemplatePropertiesBuilder withConfigService(ConfigService configService) {
    this.configService = configService;
    return this;
  }

  public TemplatePropertiesBuilder withOutgoingSms(OutgoingSms outgoingSms) {
    this.outgoingSms = outgoingSms;
    return this;
  }

  public TemplatePropertiesBuilder withTemplate(Template template) {
    this.template = template;
    return this;
  }

  public TemplatePropertiesBuilder withConfig(Config config) {
    this.config = config;
    return this;
  }

  public Map<String, Object> build() {
    Map<String, Object> props = new HashMap<>(config.getProps().size());
    props.put("recipients", template.recipientsAsString(outgoingSms.getRecipients()));
    props.put("message", escapeForJson(outgoingSms.getMessage()));
    props.put("openMrsId", outgoingSms.getOpenMrsId());
    props.put("callback", configService.getServerUrl() + "/ws/sms/status/" + config.getName());
    if (outgoingSms.getCustomParams() != null) {
      props.putAll(outgoingSms.getCustomParams());
    }

    for (ConfigProp configProp : config.getProps()) {
      props.put(configProp.getName(), configProp.getValue());
    }

    // ***** WARNING *****
    // This displays usernames & passwords in the server log! But then again, so does the settings
    // UI...
    // ***** WARNING *****
    if (LOGGER.isDebugEnabled()) {
      for (Map.Entry<String, Object> entry : props.entrySet()) {
        LOGGER.debug(String.format("PROP %s: %s", entry.getKey(), entry.getValue()));
      }
    }

    return props;
  }

  private String escapeForJson(String text) {
    return text.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}
