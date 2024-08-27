package org.connectforlife.module.sms.api.audit;

import org.connectforlife.module.sms.builder.SmsRecordBuilder;
import org.junit.Test;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SmsLoggingRecordTest extends BaseModuleContextSensitiveTest {

  private final String OpenMrsId = UUID.randomUUID().toString();
  private final String ProviderId = UUID.randomUUID().toString();
  private final String Config = "nexmo";
  private final String ErrorMessage = "Error_Message";
  private final String SmsDirection = "outbound";
  private final String MessageContent = "Message";
  private final String PhoneNo = "12345667";
  private final String TimeStamp = "12-03-2022";
  private final String ProviderStatus = "0";

  @Test
  public void checkFunctionality() {
    SmsRecord smsRecord = new SmsRecordBuilder().build();
    SmsLoggingRecord smsLoggingRecord = new SmsLoggingRecord(smsRecord);
    String smsLoggingRecordString =
        "SmsLoggingRecord{"
            + "config='"
            + Config
            + '\''
            + ", phoneNumber='"
            + PhoneNo
            + '\''
            + ", smsDirection='"
            + SmsDirection
            + '\''
            + ", timestamp='"
            + TimeStamp
            + '\''
            + ", deliveryStatus='"
            + DeliveryStatusesConstants.SCHEDULED
            + '\''
            + ", providerStatus='"
            + ProviderStatus
            + '\''
            + ", messageContent='"
            + MessageContent
            + '\''
            + ", openMrsId='"
            + OpenMrsId
            + '\''
            + ", providerId='"
            + ProviderId
            + '\''
            + ", errorMessage='"
            + ErrorMessage
            + '\''
            + '}';
    smsLoggingRecord.setConfig(Config);
    smsLoggingRecord.setDeliveryStatus(DeliveryStatusesConstants.SCHEDULED);
    smsLoggingRecord.setErrorMessage(ErrorMessage);
    smsLoggingRecord.setSmsDirection(SmsDirection);
    smsLoggingRecord.setMessageContent(MessageContent);
    smsLoggingRecord.setOpenMrsId(OpenMrsId);
    smsLoggingRecord.setPhoneNumber(PhoneNo);
    smsLoggingRecord.setProviderId(ProviderId);
    smsLoggingRecord.setTimestamp(TimeStamp);
    smsLoggingRecord.setProviderStatus(ProviderStatus);

    assertThat(Config, is(smsLoggingRecord.getConfig()));
    assertThat(PhoneNo, is(smsLoggingRecord.getPhoneNumber()));
    assertThat(MessageContent, is(smsLoggingRecord.getMessageContent()));
    assertThat(TimeStamp, is(smsLoggingRecord.getTimestamp()));
    assertThat(DeliveryStatusesConstants.SCHEDULED, is(smsLoggingRecord.getDeliveryStatus()));
    assertThat(ProviderStatus, is(smsLoggingRecord.getProviderStatus()));
    assertThat(OpenMrsId, is(smsLoggingRecord.getOpenMrsId()));
    assertThat(ProviderId, is(smsLoggingRecord.getProviderId()));
    assertThat(ErrorMessage, is(smsLoggingRecord.getErrorMessage()));
    assertThat(SmsDirection, is(smsLoggingRecord.getSmsDirection()));
    assertThat(smsLoggingRecordString, is(smsLoggingRecord.toString()));
  }
}
