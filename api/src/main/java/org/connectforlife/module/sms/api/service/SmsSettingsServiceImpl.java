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

import org.apache.commons.io.IOUtils;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.connectforlife.module.sms.api.templates.TemplateForWeb;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.json.TemplateJsonParser;
import org.connectforlife.module.sms.api.util.SMSConstants;

import java.io.IOException;
import java.util.Map;

public class SmsSettingsServiceImpl extends BaseOpenmrsService implements SmsSettingsService {

  private TemplateService templateService;

  private ConfigService configService;

  private TemplateJsonParser templateJsonParser;

  private SettingsManagerService settingsManagerService;

  @Override
  public Map<String, TemplateForWeb> getTemplates() {
    return templateService.allTemplatesForWeb();
  }

  @Override
  public void importTemplates(String templates) {
    templateJsonParser.importTemplates(templates);
  }

  @Override
  public Configs getConfigs() {
    return configService.getConfigs();
  }

  @Override
  public Configs setConfigs(Configs configs) {
    configService.updateConfigs(configs);
    return configService.getConfigs();
  }

  @Override
  public String getCustomUISettings() {
    createEmptyConfigurationIfNotExists(SMSConstants.UI_CONFIG);
    try {
      return IOUtils.toString(settingsManagerService.getRawConfig(SMSConstants.UI_CONFIG));
    } catch (IOException e) {
      throw new SmsRuntimeException(e);
    }
  }

  private void createEmptyConfigurationIfNotExists(String filename) {
    if (!settingsManagerService.configurationExist(filename)) {
      settingsManagerService.createEmptyConfiguration(filename);
    }
  }

  public void setTemplateService(TemplateService templateService) {
    this.templateService = templateService;
  }

  public void setConfigService(ConfigService configService) {
    this.configService = configService;
  }

  public void setTemplateJsonParser(TemplateJsonParser templateJsonParser) {
    this.templateJsonParser = templateJsonParser;
  }

  public void setSettingsManagerService(SettingsManagerService settingsManagerService) {
    this.settingsManagerService = settingsManagerService;
  }
}
