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
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.connectforlife.module.sms.api.templates.Template;
import org.connectforlife.module.sms.api.templates.TemplateForWeb;
import org.openmrs.api.APIException;
import org.connectforlife.module.sms.api.util.ResourceUtil;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * See {@link TemplateService}. This implementation uses the
 * OpenMRS configuration system to store the templates.
 */
public class TemplateServiceImpl implements TemplateService {

  private static final String SMS_TEMPLATE_CUSTOM_FILE_NAME = "sms-templates-custom.json";
  private static final String SMS_TEMPLATE_FILE_NAME = "sms-templates.json";
  private static final Log LOGGER = LogFactory.getLog(TemplateServiceImpl.class);
  private final SettingsManagerService settingsManagerService;
  private final List<Template> defaultTemplates = new LinkedList<>();
  private final List<Template> customTemplates = new LinkedList<>();
  private final Map<String, Template> templates = new HashMap<>();

  public TemplateServiceImpl(SettingsManagerService settingsManagerService) {
    this.settingsManagerService = settingsManagerService;
  }

  @Override
  public Template getTemplate(String name) {
    if (templates.get(name) != null) {
      return templates.get(name);
    }
    throw new IllegalArgumentException(String.format("Unknown template: '%s'.", name));
  }

  @Override
  public Map<String, TemplateForWeb> allTemplatesForWeb() {
    Map<String, TemplateForWeb> ret = new HashMap<>(templates.size());
    if (templates.isEmpty()) {
      load(SMS_TEMPLATE_CUSTOM_FILE_NAME, customTemplates);
    }
    for (Map.Entry<String, Template> entry : templates.entrySet()) {
      ret.put(entry.getKey(), new TemplateForWeb(entry.getValue()));
    }
    return ret;
  }

  @Override
  public void importTemplates(List<Template> templateList) {
    for (Template template : templateList) {
      importTemplate(template);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String jsonText = objectMapper.writeValueAsString(customTemplates);
      settingsManagerService.saveRawConfig(
          SMS_TEMPLATE_CUSTOM_FILE_NAME, new ByteArrayResource(jsonText.getBytes(StandardCharsets.UTF_8)));
    } catch (IOException ioe) {
      LOGGER.error("Failed to save loaded templates", ioe);
      throw new APIException(ioe);
    }
  }

  @Override
  public void importTemplate(Template template) {
    template.readDefaults();
    customTemplates.add(template);
    templates.put(template.getName(), template);
  }

  @Override
  public void loadTemplates() {
    LOGGER.debug("Loading the default templates");
    templates.clear();

    defaultTemplates.clear();
    load(SMS_TEMPLATE_FILE_NAME, defaultTemplates);

    customTemplates.clear();
    load(SMS_TEMPLATE_CUSTOM_FILE_NAME, customTemplates);
  }

  @Override
  public List<Template> getDefaultTemplates() {
    return defaultTemplates;
  }

  @Override
  public List<Template> getCustomTemplates() {
    return customTemplates;
  }

  @Override
  public List<Template> getAllTemplates() {
    return new LinkedList<>(templates.values());
  }

  @Override
  public void writeDefaultTemplates(List<Template> newDefaultTemplates) {
    String jsonText =
        new Gson().toJson(newDefaultTemplates, new TypeToken<List<Template>>() {}.getType());
    settingsManagerService.saveRawConfig(
        SMS_TEMPLATE_FILE_NAME, new ByteArrayResource(jsonText.getBytes(StandardCharsets.UTF_8)));
  }

  @Override
  public void writeCustomTemplates(List<Template> newCustomTemplates) {
    String jsonText =
        new Gson().toJson(newCustomTemplates, new TypeToken<List<Template>>() {}.getType());
    settingsManagerService.saveRawConfig(
        SMS_TEMPLATE_CUSTOM_FILE_NAME, new ByteArrayResource(jsonText.getBytes(StandardCharsets.UTF_8)));
  }

  private void load(final String fileName, final List<Template> loadedTemplates) {
    final List<Template> templateList;

    initializeConfig(fileName);

    try (InputStream is = settingsManagerService.getRawConfig(fileName)) {
      final String jsonText = IOUtils.toString(is);

      if (StringUtils.isNotBlank(jsonText)) {
        templateList = new Gson().fromJson(jsonText, new TypeToken<List<Template>>() {}.getType());
      } else {
        templateList = Collections.emptyList();
      }
    } catch (JsonParseException e) {
      throw new SmsRuntimeException("File " + fileName + " is malformed", e);
    } catch (IOException e) {
      throw new SmsRuntimeException("Error loading file " + fileName, e);
    }

    for (Template template : templateList) {
      template.readDefaults();
      loadedTemplates.add(template);
      templates.put(template.getName(), template);
    }
  }

  private void initializeConfig(String filename) {
    if (!settingsManagerService.configurationExist(filename)) {
      if (ResourceUtil.resourceFileExists(filename)) {
        settingsManagerService.createConfigurationFromResources(filename);
      } else {
        settingsManagerService.createEmptyConfiguration(filename);
      }
    }
  }
}
