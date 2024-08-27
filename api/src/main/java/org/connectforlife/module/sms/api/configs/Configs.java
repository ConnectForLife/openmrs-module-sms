/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.configs;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.connectforlife.module.sms.api.validate.annotation.ValidConfigs;

import java.util.ArrayList;
import java.util.List;

//
// todo: expose list of configs as task action parameter values to send_sms?
//

/** Represents all configs as well as the default config, sms-config.json */
@ValidConfigs
public class Configs {

  /**
   * The name of the default configuration that will be used, if no configuration name is explicitly
   * provided.
   */
  private String defaultConfigName;

  /** The configurations in the system. */
  @JsonProperty("configs")
  private List<Config> configList = new ArrayList<>();

  /**
   * Returns the default configuration, users can make one of the configurations default.
   *
   * @return the default configuration, never null
   * @throws IllegalStateException if no default configuration is set
   */
  @JsonIgnore
  public Config getDefaultConfig() {
    if (StringUtils.isNotBlank(defaultConfigName)) {
      return getConfig(defaultConfigName);
    }
    throw new IllegalStateException(
        "Trying to get default config, but no default config has been set.");
  }

  /**
   * Checks whether there are no configurations available.
   *
   * @return true if there are no configurations, false otherwise
   */
  @JsonIgnore
  public boolean isEmpty() {
    return configList.isEmpty();
  }

  /**
   * Returns the name of the default configuration.
   *
   * @return the name of the default configuration or null if it was not set
   */
  public String getDefaultConfigName() {
    return defaultConfigName;
  }

  /**
   * Sets the name of the default configuration.
   *
   * @param name the name of the default configuration
   */
  public void setDefaultConfigName(String name) {
    defaultConfigName = name;
  }

  /** @return all available configurations */
  public List<Config> getConfigList() {
    return configList;
  }

  /** @param configList all available configurations */
  public void setConfigList(List<Config> configList) {
    this.configList = configList;
  }

  /**
   * Fetches the config with the given name.
   *
   * @param name the name of the config
   * @return the config with the given name, never null
   * @throws IllegalArgumentException if a configuration with the given name does not exist
   */
  public Config getConfig(String name) {
    for (Config config : configList) {
      if (name.equals(config.getName())) {
        return config;
      }
    }
    throw new IllegalArgumentException("'" + name + "': no such config");
  }

  /**
   * Checks whether a configuration with the given name exists.
   *
   * @param name the name of the configuration
   * @return true if it exists, false otherwise
   */
  public boolean hasConfig(String name) {
    for (Config config : configList) {
      if (name.equals(config.getName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the config with the given name, or the default config if the provided name is null or
   * empty.
   *
   * @param name the name of the desired configuration, or null/empty string for the default
   *     configuration
   * @return the matching configuration
   * @throws IllegalStateException if a blank string is provided and there is no default
   *     configuration
   * @throws IllegalArgumentException if a configuration with the given name does not exist
   */
  public Config getConfigOrDefault(String name) {
    if (StringUtils.isBlank(name)) {
      return getDefaultConfig();
    }
    return getConfig(name);
  }
}
