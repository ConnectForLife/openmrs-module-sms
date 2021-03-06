package org.openmrs.module.sms.api.audit;

// todo: openMrsTimestamp & providerTimestamp instead of just timestamp?
// todo: 'senderNumber' & 'recipientNumber' instead of 'phoneNumber'?

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/** SMS audit record for the database. */
@Entity(name = "sms.smsRecord")
@Table(name = "sms_records")
public class SmsRecord extends AbstractBaseOpenmrsData {

  private static final String MEDIUMTEXT = "mediumtext";
  private static final long serialVersionUID = -2503300803981539221L;

  @Id
  @GeneratedValue
  @Column(name = "sms_records_id")
  private Integer id;

  /** The name of the configuration to which this SMS should be associated with. */
  @Column private String config;

  /** The direction of the SMS - either inbound or outbound. */
  @Column private SmsDirection smsDirection;

  /** The phone number to which this SMS was sent, or which it was received. */
  @Column private String phoneNumber;

  /** The content of the SMS message. */
  @Column(columnDefinition = MEDIUMTEXT)
  private String messageContent;

  /** The timestamp for this SMS. */
  @Column private Date timestamp;

  /** The delivery status for this SMS. */
  @Column private String deliveryStatus;

  @Column private String providerStatus;

  /** The ID by which OpenMRS identifies this SMS. */
  @Column private String openMrsId;

  /** The ID generated by the provider for this SMS. */
  @Column private String providerId;

  /** The error message for this SMS, if applicable. */
  @Column(columnDefinition = MEDIUMTEXT)
  private String errorMessage;

  /** Constructs a new instance of a record without filling out any fields. */
  public SmsRecord() {
    this(null, null, null, null, null, null, null, null, null, null);
  }

  /**
   * Constructs a new instance using the provided data.
   *
   * @param config the name of the configuration to which this SMS should be associated with
   * @param smsDirection the direction of the SMS - either inbound or outbound
   * @param number the phone number to which this SMS was sent, or which it was received
   * @param message the content of the SMS message
   * @param timestamp the timestamp for this SMS
   * @param deliveryStatus the delivery status for this SMS
   * @param providerStatus
   * @param openMrsId the ID by which OpenMRS identifies this SMS.
   * @param providerId the ID generated by the provider for this SMS
   * @param errorMessage the error message for this SMS, if applicable
   */
  // CHECKSTYLE:OFF: ParameterNumber
  // NO CHECKSTYLE ParameterNumber
  @SuppressWarnings({"PMD.ExcessiveParameterList"})
  public SmsRecord(
      String config,
      SmsDirection smsDirection,
      String number,
      String message,
      Date timestamp,
      String deliveryStatus,
      String providerStatus,
      String openMrsId,
      String providerId,
      String errorMessage) {
    this.config = config;
    this.smsDirection = smsDirection;
    this.phoneNumber = number;
    this.messageContent = message;
    this.timestamp = timestamp;
    this.deliveryStatus = deliveryStatus;
    this.providerStatus = providerStatus;
    this.openMrsId = openMrsId;
    this.providerId = providerId;
    this.errorMessage = errorMessage;
  }
  // CHECKSTYLE:ON: ParameterNumber

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public void setId(Integer id) {
    this.id = id;
  }

  /** @return the name of the configuration to which this SMS should be associated with */
  public String getConfig() {
    return config;
  }

  /** @param config the name of the configuration to which this SMS should be associated with */
  public void setConfig(String config) {
    this.config = config;
  }

  /** @return the direction of the SMS - either inbound or outbound */
  public SmsDirection getSmsDirection() {
    return smsDirection;
  }

  /** @param smsDirection the direction of the SMS - either inbound or outbound */
  public void setSmsDirection(SmsDirection smsDirection) {
    this.smsDirection = smsDirection;
  }

  /** @return the phone number to which this SMS was sent, or which it was received */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /** @param phoneNumber the phone number to which this SMS was sent, or which it was received */
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /** @return the content of the SMS message */
  public String getMessageContent() {
    return messageContent;
  }

  /** @param messageContent he tcontent of the SMS message */
  public void setMessageContent(String messageContent) {
    this.messageContent = messageContent;
  }

  /** @return the timestamp for this SMS */
  public Date getTimestamp() {
    return timestamp;
  }

  /** @param timestamp the timestamp for this SMS */
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  /** @return the delivery status for this SMS */
  public String getDeliveryStatus() {
    return deliveryStatus;
  }

  /** @param deliveryStatus the delivery status for this SMS */
  public void setDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
  }

  public String getProviderStatus() {
    return providerStatus;
  }

  public void setProviderStatus(String providerStatus) {
    this.providerStatus = providerStatus;
  }

  /** @return the ID by which OpenMRS identifies this SMS. */
  public String getOpenMrsId() {
    return openMrsId;
  }

  /** @param openMrsId the ID by which OpenMRS identifies this SMS. */
  public void setOpenMrsId(String openMrsId) {
    this.openMrsId = openMrsId;
  }

  /** @return the ID generated by the provider for this SMS */
  public String getProviderId() {
    return providerId;
  }

  /** @param providerId the ID generated by the provider for this SMS */
  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  /** @return the error message for this SMS */
  public String getErrorMessage() {
    return errorMessage;
  }

  /** @param errorMessage the error message for this SMS */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
