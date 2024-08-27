package org.connectforlife.module.sms.api.service;

import org.connectforlife.module.sms.api.data.AdHocSMSData;

import java.util.List;

/**
 * Service related to scheduling ad hoc SMSes actions
 */
public interface ScheduleAdHocSMSesService {

  /**
   * Schedules AdHoc SMSes based on list of {@link AdHocSMSData} objects
   *
   * @param smsData list of {@link AdHocSMSData} objects
   */
  void scheduleAdHocSMSes(List<AdHocSMSData> smsData);
}
