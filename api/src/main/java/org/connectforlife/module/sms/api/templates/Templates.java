/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.templates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Helper template collection. */
public class Templates {

  /** Templates as map, where keys are their names. */
  private Map<String, Template> templateMap = new HashMap<>();

  /**
   * Constructs this collection from the provided templates. The templates will read their default
   * values from the OpenMRS configuration system through the provided settings facade.
   *
   * @param templates the collection of templates from which this object will be built
   */
  public Templates(List<Template> templates) {
    for (Template template : templates) {
      template.readDefaults();
      this.templateMap.put(template.getName(), template);
    }
  }

  /**
   * Returns the template with the given name.
   *
   * @param name the name of the template
   * @return the matching template or null if no such template exists
   */
  public Template getTemplate(String name) {
    return templateMap.get(name);
  }

  /**
   * Returns this collection in a form suitable for the UI.
   *
   * @return a map where the keys are template names and values are the simplified forms of
   *     templates
   * @see TemplateForWeb
   */
  public Map<String, TemplateForWeb> templatesForWeb() {
    Map<String, TemplateForWeb> ret = new HashMap<>(templateMap.size());
    for (Map.Entry<String, Template> entry : templateMap.entrySet()) {
      ret.put(entry.getKey(), new TemplateForWeb(entry.getValue()));
    }
    return ret;
  }
}
