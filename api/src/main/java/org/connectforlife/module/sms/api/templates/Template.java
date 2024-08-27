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

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.connectforlife.module.sms.api.util.OutgoingRequestBuilder;

import java.util.List;
import java.util.Map;

/**
 * Models how we can talk to a specific SMS provider.
 *
 * <p>The Template and related classes have verbose Jackson annotations for clear distinction
 * between properties from JSON files and properties calculated/read from other sources during
 * runtime.
 */
public class Template {

  /** Models the handling of outgoing SMS messages. */
  @JsonProperty private Outgoing outgoing;

  /** Models how the status updates from the provider are being handled. */
  @JsonProperty private Status status;

  /** Models how incoming SMS messages are being handled. */
  @JsonProperty private Incoming incoming;

  /** The unique name of the template. */
  @JsonProperty private String name;

  /** Configurable values that the user can edit on the UI. */
  @JsonProperty private List<String> configurables;

  /**
   * Generates an HTTP request for an outgoing SMS from the provided properties.
   *
   * @param props the properties used for building the request
   * @return the HTTP request to execute
   */
  public HttpMethod generateRequestFor(Map<String, Object> props) {
    return new OutgoingRequestBuilder()
        .withTemplateName(name)
        .withOutgoingRequest(outgoing.getRequest())
        .withProps(props)
        .build();
  }

  /**
   * Formats the recipient list into a single string that can be understood by the provider.
   *
   * @param recipients the list of recipients
   * @return the recipient string for the provider
   */
  public String recipientsAsString(Iterable<String> recipients) {
    return StringUtils.join(recipients.iterator(), outgoing.getRequest().getRecipientsSeparator());
  }

  /** @return the {@link Outgoing} instance used by this template for outgoing SMS messages */
  public Outgoing getOutgoing() {
    return outgoing;
  }

  /** @return the {@link Incoming} instance used by this template for incoming SMS messages */
  public Incoming getIncoming() {
    return incoming;
  }

  /** @return the unique name of this template */
  public String getName() {
    return name;
  }

  /** @param name the unique name of this template */
  public void setName(String name) {
    this.name = name;
  }

  /** @return configurable values that the user can edit on the UI */
  public List<String> getConfigurables() {
    return configurables;
  }

  /** @return the {@link Status} object used for dealing with status updates from the provider */
  public Status getStatus() {
    return status;
  }

  /**
   * @param status the {@link Status} object used for dealing with status updates from the provider
   */
  public void setStatus(Status status) {
    this.status = status;
  }

  /** Reads the default values from OpenMRS settings, updating this template. */
  public void readDefaults() {
    outgoing.readDefaults();
  }

  public void setOutgoing(Outgoing outgoing) {
    this.outgoing = outgoing;
  }

  public void setIncoming(Incoming incoming) {
    this.incoming = incoming;
  }

  public void setConfigurables(List<String> configurables) {
    this.configurables = configurables;
  }

  @Override
  public String toString() {
    return "Template{"
        + "outgoing="
        + outgoing
        + ", status="
        + status
        + ", incoming="
        + incoming
        + ", name='"
        + name
        + '\''
        + ", configurables="
        + configurables
        + '}';
  }
}
