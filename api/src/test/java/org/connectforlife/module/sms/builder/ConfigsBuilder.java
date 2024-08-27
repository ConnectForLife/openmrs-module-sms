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

import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.configs.Configs;

import java.util.Arrays;
import java.util.List;

public class ConfigsBuilder extends AbstractBuilder<Configs> {

  private static final String NEXMO_GENERIC = "nexmo-generic";
  private static final String NEXMO_MULTILINE = "nexmo-multiline";
  private List<Config> configs;
  private String defaultConfigName;

  public ConfigsBuilder() {
    Config config = new ConfigBuilder().buildAsNew();
    this.configs = buildConfigs(config);
    this.defaultConfigName = config.getName();
  }

  @Override
  public Configs build() {
    Configs configurations = new Configs();
    configurations.setConfigList(this.configs);
    configurations.setDefaultConfigName(this.defaultConfigName);
    return configurations;
  }

  @Override
  public Configs buildAsNew() {
    return build();
  }

  private List<Config> buildConfigs(Config defaultConfig) {
    Config genericConfig =
        new ConfigBuilder().withName(NEXMO_GENERIC).withTemplateName(NEXMO_GENERIC).buildAsNew();
    Config multilineConfig =
        new ConfigBuilder()
            .withName(NEXMO_MULTILINE)
            .withTemplateName(NEXMO_MULTILINE)
            .buildAsNew();
    return Arrays.asList(defaultConfig, genericConfig, multilineConfig);
  }

  public List<Config> getConfigs() {
    return configs;
  }

  public ConfigsBuilder withConfigs(List<Config> configs) {
    this.configs = configs;
    return this;
  }

  public String getDefaultConfigName() {
    return defaultConfigName;
  }

  public ConfigsBuilder withDefaultConfigName(String defaultConfigName) {
    this.defaultConfigName = defaultConfigName;
    return this;
  }
}
