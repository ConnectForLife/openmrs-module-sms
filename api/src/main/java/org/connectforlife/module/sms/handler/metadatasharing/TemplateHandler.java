/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.handler.metadatasharing;

import org.apache.commons.collections.CollectionUtils;
import org.connectforlife.module.sms.api.templates.Template;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;
import org.connectforlife.module.sms.api.service.TemplateService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The TemplateHandler Class is a Handler which exposes {@link Template}s to be exported and imported by Metadata Sharing
 * module.
 * <p>
 * Bean configured in moduleApplicationContext.xml
 * </p>
 *
 * @implNote The Metadata Sharing handlers us an ID an UUID to read the objects to import and export.
 * Since Configuration Template has no ID or UUID and there is no validation for UUID, these values are generated as follows:
 * the ID is equal to hashCode of the name; the UUID is equal to the name.
 * New templates are always added to default templates.
 */
@OpenmrsProfile(modules = {"metadatasharing:1.*"})
public class TemplateHandler
        implements MetadataTypesHandler<Template>, MetadataSearchHandler<Template>, MetadataSaveHandler<Template>,
        MetadataPropertiesHandler<Template>, MetadataMergeHandler<Template> {
  private final Map<Class<? extends Template>, String> types;

  public TemplateHandler() {
    final Map<Class<? extends Template>, String> tmpTypes = new HashMap<>();
    tmpTypes.put(Template.class, "SMS Provider Template");
    this.types = Collections.unmodifiableMap(tmpTypes);
  }

  @Override
  public Integer getId(Template template) {
    return template.getName().hashCode();
  }

  @Override
  public void setId(Template object, Integer id) {
    // Nothing to do
  }

  @Override
  public String getUuid(Template template) {
    return template.getName();
  }

  @Override
  public void setUuid(Template object, String uuid) {
    // Nothing to do
  }

  @Override
  public Boolean getRetired(Template object) {
    return Boolean.FALSE;
  }

  @Override
  public void setRetired(Template object, Boolean retired) {
    //Nothing to do
  }

  @Override
  public String getName(Template template) {
    return template.getName();
  }

  @Override
  public String getDescription(Template template) {
    return null;
  }

  @Override
  public Date getDateChanged(Template object) {
    return null;
  }

  @Override
  public Map<String, Object> getProperties(Template template) {
    return Collections.emptyMap();
  }

  @Override
  public Template saveItem(Template item) {
    final TemplateService templateService = Context.getService(TemplateService.class);

    final Template savedItem;

    final List<Template> defaultTemplates = templateService.getDefaultTemplates();
    final Template savedDefaultTemplate = saveIfExists(defaultTemplates, item);

    if (savedDefaultTemplate != null) {
      // Updated default
      savedItem = savedDefaultTemplate;
      templateService.writeDefaultTemplates(defaultTemplates);
    } else {
      final List<Template> customTemplates = templateService.getCustomTemplates();
      final Template savedCustomTemplate = saveIfExists(customTemplates, item);

      if (savedCustomTemplate != null) {
        // Updated custom
        savedItem = savedCustomTemplate;
        templateService.writeCustomTemplates(customTemplates);
      } else {
        // New default item
        defaultTemplates.add(item);
        savedItem = item;
        templateService.writeDefaultTemplates(defaultTemplates);
      }
    }

    templateService.loadTemplates();
    return savedItem;
  }

  private Template saveIfExists(final List<Template> templates, final Template item) {
    final Template template = findByName(templates, item.getName());

    if (template != null) {
      return updateTemplate(template, item);
    }

    return null;
  }

  private Template updateTemplate(final Template target, final Template source) {
    target.setOutgoing(source.getOutgoing());
    target.setStatus(source.getStatus());
    target.setIncoming(source.getIncoming());
    target.setConfigurables(source.getConfigurables());
    return target;
  }

  private Template findByName(final Iterable<Template> templates, final String name) {
    for (final Template template : templates) {
      if (template.getName().equals(name)) {
        return template;
      }
    }

    return null;
  }

  @Override
  public int getItemsCount(Class<? extends Template> type, boolean includeRetired, String phrase) {
    return Context.getService(TemplateService.class).getAllTemplates().size();
  }

  @Override
  public List<Template> getItems(Class<? extends Template> type, boolean includeRetired, String phrase, Integer firstResult,
                                 Integer maxResults) throws DAOException {
    final List<Template> allTemplates = Context.getService(TemplateService.class).getAllTemplates();

    final int start = firstResult == null ? 0 : firstResult;
    final int end = maxResults == null ? allTemplates.size() : Math.min(allTemplates.size(), start + maxResults);

    return allTemplates.subList(start, end);
  }

  @Override
  public Template getItemByUuid(Class<? extends Template> type, String uuid) {
    final List<Template> allTemplates = Context.getService(TemplateService.class).getAllTemplates();

    for (final Template template : allTemplates) {
      if (template.getName().equals(uuid)) {
        return template;
      }
    }

    return null;
  }

  @Override
  public Template getItemById(Class<? extends Template> type, Integer id) {
    final List<Template> allTemplates = Context.getService(TemplateService.class).getAllTemplates();

    for (final Template template : allTemplates) {
      if (template.getName().hashCode() == id) {
        return template;
      }
    }

    return null;
  }

  @Override
  public Map<Class<? extends Template>, String> getTypes() {
    return types;
  }

  @Override
  public int getPriority() {
    // Used to choose Handler in case of multiple for the same types
    return 0;
  }

  @Override
  public void merge(Template existing, Template incoming, ImportType importType, Map<Object, Object> incomingToExisting) {
    if (importType.isOverwriteMine() || importType.isPreferTheirs()) {
      existing.setConfigurables(incoming.getConfigurables());
      existing.setOutgoing(incoming.getOutgoing());
      existing.setIncoming(incoming.getIncoming());
    } else if (importType.isPreferMine()) {
      if (CollectionUtils.isEmpty(existing.getConfigurables())) {
        existing.setConfigurables(incoming.getConfigurables());
      }
      if (existing.getOutgoing() == null) {
        existing.setOutgoing(incoming.getOutgoing());
      }
      if (existing.getIncoming() == null) {
        existing.setIncoming(incoming.getIncoming());
      }
    }
  }
}
