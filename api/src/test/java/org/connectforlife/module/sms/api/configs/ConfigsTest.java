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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConfigsTest {

  private static final String NAME_ONE = "one";
  private static final String NAME_TWO = "two";
  private static final String NAME_THREE = "three";
  private Config configOne;
  private Config configTwo;
  private List<Config> configList = new ArrayList<>();

  @Before
  public void setup() {
    configOne = new Config();
    configOne.setName(NAME_ONE);
    configTwo = new Config();
    configTwo.setName(NAME_TWO);
    configList.add(configOne);
    configList.add(configTwo);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowWhenGettingDefaultConfigWhileNoneSet() {
    Configs emptyConfigs = new Configs();
    emptyConfigs.getDefaultConfig();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowWhenGettingDefaultConfigWithConfigsWhileNoneSetEven() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    configs.getDefaultConfig();
  }

  @Test
  public void shouldReturnConfig() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    assertEquals(configTwo, configs.getConfig(NAME_TWO));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowWhenGettingInvalidConfig() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    configs.getConfig(NAME_THREE);
  }

  @Test
  public void shouldHaveConfig() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    assertEquals(true, configs.hasConfig(NAME_TWO));
  }

  @Test
  public void shouldNotHaveConfig() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    assertEquals(false, configs.hasConfig(NAME_THREE));
  }

  @Test
  public void shouldReturnDefaultConfig() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    configs.setDefaultConfigName(configList.get(configList.indexOf(configTwo)).getName());
    assertEquals(configTwo, configs.getDefaultConfig());
  }

  @Test
  public void shouldReturnDefaultConfigWhenGivenNull() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    configs.setDefaultConfigName(configList.get(configList.indexOf(configTwo)).getName());
    assertEquals(configTwo, configs.getConfigOrDefault(null));
  }

  @Test
  public void shouldReturnDefaultConfigWhenGivenNonNull() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    configs.setDefaultConfigName(configList.get(configList.indexOf(configTwo)).getName());
    assertEquals(configTwo, configs.getConfigOrDefault(NAME_TWO));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowWhenGettingInvalidConfigOrDefault() {
    Configs configs = new Configs();
    configs.setConfigList(configList);
    configs.setDefaultConfigName(configList.get(configList.indexOf(configTwo)).getName());
    configs.getConfigOrDefault(NAME_THREE);
  }
}
