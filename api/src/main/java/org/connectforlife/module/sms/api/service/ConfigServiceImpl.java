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

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.util.SMSConstants;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** See {@link ConfigService} */
public class ConfigServiceImpl extends BaseOpenmrsService implements ConfigService {

  private static final Log LOGGER = LogFactory.getLog(ConfigServiceImpl.class);

  private SettingsManagerService settingsManagerService;

  private Configs configs;

  private synchronized void loadConfigs() {
    loadDefaultConfigurationIfNotExists(SMSConstants.SMS_CONFIGS_FILE_NAME);
    try (InputStream is = settingsManagerService.getRawConfig(SMSConstants.SMS_CONFIGS_FILE_NAME)) {
      String jsonText = IOUtils.toString(is);
      Gson gson = new Gson();
      configs = gson.fromJson(jsonText, Configs.class);
    } catch (IOException ioException) {
      throw new JsonIOException(
          "Malformed " + SMSConstants.SMS_CONFIGS_FILE_NAME + " file? " + ioException, ioException);
    }
  }

  public ConfigServiceImpl(SettingsManagerService settingsManagerService) {
    this.settingsManagerService = settingsManagerService;
    loadConfigs();
  }

  public Config getDefaultConfig() {
    return configs.getDefaultConfig();
  }

  public List<Config> getConfigList() {
    return configs.getConfigList();
  }

  public Configs getConfigs() {
    return configs;
  }

  public boolean hasConfig(String name) {
    return configs.hasConfig(name);
  }

  public Config getConfig(String name) {
    return configs.getConfig(name);
  }

  public Config getConfigOrDefault(String name) {
    return configs.getConfigOrDefault(name);
  }

  public void updateConfigs(Configs configs) {
    Gson gson = new Gson();
    String jsonText = gson.toJson(configs, Configs.class);
    ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes(StandardCharsets.UTF_8));
    settingsManagerService.saveRawConfig(SMSConstants.SMS_CONFIGS_FILE_NAME, resource);
    loadConfigs();
  }

  public boolean hasConfigs() {
    return !configs.isEmpty();
  }

  @Override
  public String getServerUrl() {
    String serverUrl =
        Context.getAdministrationService().getGlobalProperty(SMSConstants.SMS_SERVER_URL);

    if (StringUtils.isEmpty(serverUrl)) {
      String message =
          String.format(
              "The %s global setting need to be set. Default sms server url has been " + "used: %s",
              SMSConstants.SMS_SERVER_URL, SMSConstants.DEFAULT_SMS_SERVER_URL);
      LOGGER.warn(message);
      serverUrl = SMSConstants.DEFAULT_SMS_SERVER_URL;
    }
    return serverUrl;
  }

  private void loadDefaultConfigurationIfNotExists(String filename) {
    if (!settingsManagerService.configurationExist(filename)) {
      settingsManagerService.createConfigurationFromResources(filename);
    }
  }
}
