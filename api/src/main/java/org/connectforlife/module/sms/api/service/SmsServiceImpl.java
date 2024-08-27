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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.templates.Template;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.connectforlife.module.sms.api.audit.SmsDirection;
import org.connectforlife.module.sms.api.audit.SmsRecord;
import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.dao.SmsRecordDao;
import org.connectforlife.module.sms.api.event.SmsEvent;
import org.connectforlife.module.sms.api.task.SmsScheduledTask;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.OpenMRSIDGeneratorUtil;
import org.connectforlife.module.sms.api.util.SmsEventParamsConstants;
import org.connectforlife.module.sms.api.util.SmsEventSubjectsConstants;
import org.connectforlife.module.sms.api.util.SmsEventsHelper;
import org.springframework.transaction.annotation.Transactional;


/**
 * Send an SMS, we really don't send here, but rather pass it on to the SmsHttpService which does
 */
public class SmsServiceImpl extends BaseOpenmrsService implements SmsService {

  private static final Log LOGGER = LogFactory.getLog(SmsServiceImpl.class);

  private SmsEventService smsEventService;
  private SmsSchedulerService schedulerService;
  private TemplateService templateService;
  private ConfigService configService;
  private SmsRecordDao smsRecordDao;

  public SmsServiceImpl(
      SmsEventService smsEventService,
      SmsSchedulerService schedulerService,
      TemplateService templateService,
      ConfigService configService,
      SmsRecordDao smsRecordDao) {
    this.smsEventService = smsEventService;
    this.schedulerService = schedulerService;
    this.templateService = templateService;
    this.configService = configService;
    this.smsRecordDao = smsRecordDao;
  }

  private static List<String> splitMessage(
      String message, int maxSize, String header, String footer, boolean excludeLastFooter) {
    List<String> parts = new ArrayList<>();
    int messageLength = message.length();

    if (messageLength <= maxSize) {
      parts.add(message);
    } else {
      // NOTE: since the format placeholders $m and $t are two characters wide and we assume no more
      // than
      // 99 parts, we don't need to do a String.format() to figure out the length of the actual
      // header/footer
      String headerTemplate = header + "\n";
      String footerTemplate = "\n" + footer;
      int textSize = maxSize - headerTemplate.length() - footerTemplate.length();
      Integer numberOfParts = (int) Math.ceil(messageLength / (double) textSize);
      String numberOfPartsString = numberOfParts.toString();

      for (Integer i = 1; i <= numberOfParts; i++) {
        StringBuilder sb = new StringBuilder();
        sb.append(headerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
        if (i.equals(numberOfParts)) {
          sb.append(message.substring((i - 1) * textSize));
          if (!excludeLastFooter) {
            sb.append(
                footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
          }
        } else {
          sb.append(message.substring((i - 1) * textSize, (i - 1) * textSize + textSize));
          sb.append(footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
        }
        parts.add(sb.toString());
      }
    }
    return parts;
  }

  private List<List<String>> splitRecipientList(List<String> list, Integer maxSize) {
    List<List<String>> ret = new ArrayList<>();
    int i = 0;
    ArrayList<String> chunk = new ArrayList<>();
    for (String val : list) {
      chunk.add(val);
      i++;
      if (i % maxSize == 0) {
        ret.add(chunk);
        chunk = new ArrayList<>();
      }
    }
    if (!chunk.isEmpty()) {
      ret.add(chunk);
    }
    return ret;
  }

  /** Sends an SMS */
  @Override
  @Transactional
  public void send(OutgoingSms sms) {

    if (!configService.hasConfigs()) {
      String message =
          String.format(
              "Trying to send an SMS, but there are no SMS configs on this server. "
                  + "outgoingSms = %s",
              sms.toString());
      throw new IllegalStateException(message);
    }

    Config config = configService.getConfigOrDefault(sms.getConfig());
    Template template = templateService.getTemplate(config.getTemplateName());

    // todo: die if things aren't right, right?
    // todo: SMS_SCHEDULE_FUTURE_SMS research if any sms provider provides that, for now assume not.

    Integer maxSize = template.getOutgoing().getMaxSmsSize();
    String header = config.getSplitHeader();
    String footer = config.getSplitFooter();
    Boolean excludeLastFooter = config.getExcludeLastFooter();
    // todo: maximum number of supported recipients : per template/provider and/or per http specs
    verifyMessageLength(maxSize, header, footer);

    List<String> messageParts =
        splitMessage(sms.getMessage(), maxSize, header, footer, excludeLastFooter);
    List<List<String>> recipientsList =
        splitRecipientList(sms.getRecipients(), template.getOutgoing().getMaxRecipient());

    // todo: delivery_time on the sms provider's side if they support it?
    for (List<String> recipients : recipientsList) {
      if (sms.hasDeliveryTime().equals(Boolean.TRUE)) {
        scheduleSms(sms, config, messageParts, recipients);
      } else {
        for (String part : messageParts) {
          String openMrsId = OpenMRSIDGeneratorUtil.generateOpenMRSID();
          smsEventService.sendEventMessage(
             SmsEventsHelper.outboundEvent(
                  SmsEventSubjectsConstants.PENDING,
                  config.getName(),
                  recipients,
                  part.replace('\n', ' '),
                  openMrsId,
                  null,
                  null,
                  null,
                  null,
                  sms.getCustomParams()));
          LOGGER.info(
              String.format(
                  "Sending message [%s] to [%s].", part.replace("\n", "\\n"), recipients));
          for (String recipient : recipients) {
            smsRecordDao.createOrUpdate(
                new SmsRecord(
                    config.getName(),
                    SmsDirection.OUTBOUND,
                    recipient,
                    part,
                    DateUtil.now(),
                    DeliveryStatusesConstants.PENDING,
                    null,
                    openMrsId,
                    null,
                    null));
          }
        }
      }
    }
  }

  private void scheduleSms(
      OutgoingSms sms, Config config, List<String> messageParts, List<String> recipients) {
    Date dt = sms.getDeliveryTime();
    for (String part : messageParts) {
      String openMrsId = OpenMRSIDGeneratorUtil.generateOpenMRSID();
      SmsEvent event =
         SmsEventsHelper.outboundEvent(
              SmsEventSubjectsConstants.SCHEDULED,
              config.getName(),
              recipients,
              part,
              openMrsId,
              null,
              null,
              null,
              null,
              sms.getCustomParams());
      // OpenMRS scheduler needs unique job ids, so adding openMrsId as job_id_key will do that
      event.getParameters().put(SmsEventParamsConstants.OPENMRS_ID, openMrsId);
      event.getParameters().put(SmsEventParamsConstants.DELIVERY_TIME, dt);
      schedulerService.safeScheduleRunOnceJob(event, dt, new SmsScheduledTask());
      LOGGER.info(
          String.format(
              "Scheduling message [%s] to [%s] at %s.",
              part.replace("\n", "\\n"), recipients, sms.getDeliveryTime()));
      // add one millisecond to the next sms part so they will be delivered in order
      // without that it seems Quartz doesn't fire events in the order they were scheduled
      dt = DateUtil.plusDays(dt, 1);
      for (String recipient : recipients) {
        smsRecordDao.createOrUpdate(
            new SmsRecord(
                config.getName(),
                SmsDirection.OUTBOUND,
                recipient,
                part,
                DateUtil.now(),
                DeliveryStatusesConstants.SCHEDULED,
                null,
                openMrsId,
                null,
                null));
      }
    }
  }

  private void verifyMessageLength(Integer maxSize, String header, String footer) {
    // todo - cr - move that to the Config object so calculated only once ?
    // todo - cr - investigate if that might be a problem on windows
    // -2 to account for the added \n after the header and before the footer
    if ((maxSize - header.length() - footer.length() - 2) <= 0) {
      throw new IllegalArgumentException(
          "The combined sizes of the header and footer templates are larger than the maximum SMS size!");
    }
  }
}
