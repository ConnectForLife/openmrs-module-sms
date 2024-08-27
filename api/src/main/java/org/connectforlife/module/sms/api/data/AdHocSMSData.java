package org.connectforlife.module.sms.api.data;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdHocSMSData {

  public static final String PHONE_ALIAS = "Phone";

  public static final String SMS_TEXT_ALIAS = "SMS Text";

  public static final String PARAMETERS_ALIAS = "Parameters";

  public static final String CONTACT_TIME_ALIAS = "Contact time";

  public static final String CONFIG_ALIAS = "Config";

  public static final List<String> COLUMN_NAMES = Arrays.asList(PHONE_ALIAS, SMS_TEXT_ALIAS,
      PARAMETERS_ALIAS, CONTACT_TIME_ALIAS, CONFIG_ALIAS);

  @JsonProperty(PHONE_ALIAS)
  private String phone;

  @JsonProperty(SMS_TEXT_ALIAS)
  private String smsText;

  @JsonProperty(PARAMETERS_ALIAS)
  private Map<String, Object> parameters;

  @JsonProperty(CONTACT_TIME_ALIAS)
  private String contactTime;

  @JsonProperty(CONFIG_ALIAS)
  private String config;

  public AdHocSMSData() {
  }

  public AdHocSMSData(Map<String, String> resultMap) throws IOException {
    this.phone = resultMap.get(PHONE_ALIAS);
    this.smsText = resultMap.get(SMS_TEXT_ALIAS);
    this.parameters = new ObjectMapper().readValue(resultMap.get(PARAMETERS_ALIAS), Map.class);
    this.contactTime = resultMap.get(CONTACT_TIME_ALIAS);
    this.config = resultMap.get(CONFIG_ALIAS);
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getSmsText() {
    return smsText;
  }

  public void setSmsText(String smsText) {
    this.smsText = smsText;
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  @JsonIgnore
  public void setParameters(String parameters) throws IOException {
    this.parameters = new ObjectMapper().readValue(parameters, Map.class);
  }

  public String getContactTime() {
    return contactTime;
  }

  public void setContactTime(String contactTime) {
    this.contactTime = contactTime;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }
}
