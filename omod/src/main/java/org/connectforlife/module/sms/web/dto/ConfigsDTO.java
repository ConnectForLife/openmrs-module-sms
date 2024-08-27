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

package org.connectforlife.module.sms.web.dto;

import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.configs.Configs;

import java.util.ArrayList;
import java.util.List;

public class ConfigsDTO {
  private List<Config> configs;
  private String defaultConfigName;

  public ConfigsDTO() {
    this.configs = new ArrayList<>();
  }

  public ConfigsDTO(Configs configs) {
    this.configs = configs.getConfigList();
    this.defaultConfigName = configs.getDefaultConfigName();
  }

  public Configs newConfigs() {
    final Configs newConfigs = new Configs();
    newConfigs.setConfigList(this.getConfigs());
    newConfigs.setDefaultConfigName(defaultConfigName);
    return newConfigs;
  }

  public List<Config> getConfigs() {
    return configs;
  }

  public void setConfigs(List<Config> configs) {
    this.configs = configs;
  }

  public String getDefaultConfigName() {
    return defaultConfigName;
  }

  public void setDefaultConfigName(String defaultConfigName) {
    this.defaultConfigName = defaultConfigName;
  }
}
