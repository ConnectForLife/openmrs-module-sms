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
import org.connectforlife.module.sms.api.configs.ConfigProp;

import java.util.ArrayList;
import java.util.List;

public class ConfigBuilder extends AbstractBuilder<Config> {

    private String name;
    private Integer maxRetries;
    private Boolean excludeLastFooter;
    private String splitHeader;
    private String splitFooter;
    private String templateName;
    private List<ConfigProp> props;

    public ConfigBuilder() {
        this.name = "nexmo";
        this.maxRetries = 3;
        this.excludeLastFooter = true;
        this.splitHeader = "Msg $m of $t";
        this.splitFooter = "...";
        this.templateName = "nexmo";
        this.props = new ArrayList<>();
        this.props.add(new ConfigPropBuilder()
                .withName("from")
                .withValue("48223551002")
                .build());
        this.props.add(new ConfigPropBuilder()
                .withName("api_key")
                .withValue("YOUR_API_KEY")
                .build());
        this.props.add(new ConfigPropBuilder()
                .withName("api_secret")
                .withValue("YOUR_API_SECRET")
                .build());
    }

    @Override
    public Config build() {
        Config config = new Config();
        config.setName(this.name);
        config.setMaxRetries(this.maxRetries);
        config.setExcludeLastFooter(this.excludeLastFooter);
        config.setSplitHeader(this.splitHeader);
        config.setSplitFooter(this.splitFooter);
        config.setTemplateName(this.templateName);
        config.setProps(this.props);
        return config;
    }

    @Override
    public Config buildAsNew() {
        return build();
    }

    public String getName() {
        return name;
    }

    public ConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public ConfigBuilder withMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public Boolean getExcludeLastFooter() {
        return excludeLastFooter;
    }

    public ConfigBuilder withExcludeLastFooter(Boolean excludeLastFooter) {
        this.excludeLastFooter = excludeLastFooter;
        return this;
    }

    public String getSplitHeader() {
        return splitHeader;
    }

    public ConfigBuilder withSplitHeader(String splitHeader) {
        this.splitHeader = splitHeader;
        return this;
    }

    public String getSplitFooter() {
        return splitFooter;
    }

    public ConfigBuilder withSplitFooter(String splitFooter) {
        this.splitFooter = splitFooter;
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public ConfigBuilder withTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public List<ConfigProp> getProps() {
        return props;
    }

    public ConfigBuilder withProps(List<ConfigProp> props) {
        this.props = props;
        return this;
    }
}
