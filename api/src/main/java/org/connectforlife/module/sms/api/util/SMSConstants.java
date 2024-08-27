/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.util;

public final class SMSConstants {

  public static final String VIEW_SMS_LOGS_PERMISSION = "viewSMSLogs";

  public static final String UI_CONFIG = "custom-ui.js";

  public static final String SMS_CONFIGS_FILE_NAME = "sms-configs.json";

  public static final String CONFIG_DIR = "sms_config";

  public static final String CONFIG_FILE_PATH = CONFIG_DIR + "/" + SMS_CONFIGS_FILE_NAME;

  public static final String FILE_PATH = "file.path";

  public static final String SMS_SERVER_URL = "sms_server_url";

  public static final String DEFAULT_SMS_SERVER_URL = "http://localhost:8080/openmrs";

  public static final String PARAM_JOB_ID = "JobID";

  public static final String SMS_DEFAULT_MILLISECONDS_BETWEEN_MESSAGES_KEY =
      "sms.default.millisecond_between_messages";

  public static final String SMS_DEFAULT_MAX_SMS_SIZE_KEY = "sms.default.max_sms_size";

  public static final String SMS_DEFAULT_MAX_RECIPIENT_KEY = "sms.default.max_recipient";

  public static final String SMS_DEFAULT_RECIPIENT_SEPARATOR_KEY =
      "sms.default.recipient_separator";

  public static final String SMS_DEFAULT_MILLISECONDS_BETWEEN_MESSAGES_VALUE = "1";

  public static final String SMS_DEFAULT_MAX_SMS_SIZE_VALUE = "160";

  public static final String SMS_DEFAULT_MAX_RECIPIENT_VALUE = "1";

  public static final String SMS_DEFAULT_RECIPIENT_SEPARATOR_VALUE = ",";

  public static final String DEFAULT_USER_TIMEZONE = "sms.defaultUserTimezone";

  public static final String DEFAULT_USER_TIMEZONE_DEFAULT_VALUE = "Etc/GMT";

  public static final String DEFAULT_USER_TIMEZONE_DESCRIPTION =
      "The timezone which represents end user timezone."
          + " The sms module use this value to interpreted time on UI";

  public static final int SMS_DEFAULT_RETRY_COUNT = 3;

  /**
   * The name of Global Property which specifies if all incoming messages handlers have to be
   * disabled. Disabling all handlers, means any SMS/WhatsApp communication coming to the system is
   * going to be ignored (the audit is still performed, the incoming messages are saved in DB!).
   */
  public static final String GP_DISABLE_INCOMING_MESSAGE_HANDLERS =
      "sms.incoming.message.handlers.disabled";

  /**
   * User-friendly description of {@link #GP_DISABLE_INCOMING_MESSAGE_HANDLERS}.
   */
  public static final String GP_DISABLE_INCOMING_MESSAGE_HANDLERS_DESC =
      "Specifies if all incoming message handlers have to be disabled. "
          + "Disabling all handlers, means any SMS/WhatsApp communication coming to the system is going to "
          + "be ignored (the audit is still performed, the incoming messages are saved in DB!). Default: false";

  /**
   * The default value of {@link #GP_DISABLE_INCOMING_MESSAGE_HANDLERS}.
   */
  public static final String GP_DISABLE_INCOMING_MESSAGE_HANDLERS_DEFAULT_VALUE = "false";

  /**
   * The name of Global Parameter with a map of Spring beans which are added to all Apache Velocity
   * contexts for all evaluations done in SMS module. Including, the default evaluation of automatic
   * SMS response.
   */
  public static final String GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP =
      "sms.velocity-template.context.beans.map";

  public static final String GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP_DESC =
      "The Global Parameter with a map of Spring beans which are added to all Apache Velocity "
          + "contexts for all evaluations done in SMS module. Including, the default evaluation of automatic "
          + "SMS response. The value is a comma separated list of entries, where each entry follows pattern: "
          + "\"<name>:<bean_name_or_class_name>\", the bean is going to be available for usage in Velocity Templates under"
          + " a 'name'.";

  public static final String GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP_DEFAULT_VALUE = "";

  public static final String SCHEDULED_SMS_MESSAGES_DETAILS_GP_KEY = "sms.scheduledMessagesDetails";

  public static final String SCHEDULED_SMS_MESSAGES_DETAILS_GP_DEFAULT_VALUE = "[{}]";

  public static final String SCHEDULED_SMS_MESSAGES_DETAILS_GP_DESCRIPTION = "Provides details for scheduled SMS messages";

  private SMSConstants() {
  }
}
