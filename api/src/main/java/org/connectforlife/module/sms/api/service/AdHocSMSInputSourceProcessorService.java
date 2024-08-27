package org.connectforlife.module.sms.api.service;

import java.util.List;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSInputSourceProcessor;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSInputSourceProcessorContext;
import org.connectforlife.module.sms.api.data.AdHocSMSData;

/**
 * General interface for ad hoc SMS input source processors stuff
 */
public interface AdHocSMSInputSourceProcessorService {

  /**
   * Delegates work to concrete implementation of {@link AdHocSMSInputSourceProcessor} class and
   * gets ad hoc SMS data
   *
   * @param context input source context data
   * @return list of {@link AdHocSMSData} objects
   */
  List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context);
}
