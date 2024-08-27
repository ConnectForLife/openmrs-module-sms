/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.event;

import org.junit.Before;
import org.junit.Test;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.SmsEventParamsConstants;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SmsEventTest {

  private static final String CONFIG = "config";
  private static final String MESSAGE = "message";
  private static final String OPENMRS_ID = "openmrs_id";
  private static final String PROVIDER_MESSAGE_ID = "provider_message_id";
  private static final Integer FAILURE_COUNT = 2;

  private static final List<String> RECIPIENTS_LIST = Arrays.asList("recipient1", "recipient2");
  private static final String RECIPIENTS_LIST_AS_STRING = "recipient1,recipient2";

  private static final Map<String, String> CUSTOM_PARAMS =
      new HashMap<String, String>() {
        {
          put("key1", "value1");
          put("key2", "value2");
        }
      };
  private static final String CUSTOM_PARAMS_AS_STRING = "key1=value1,key2=value2";

  private static final Date DELIVERY_TIME = DateUtil.parse("2012-01-10T00:00:00.000+06:00");
  private static final String DELIVERY_TIME_AS_STRING = "2012-01-09T18:00:00.000Z";
  private static final String DELIVERY_TIME_ZONE = "Asia/Almaty";

  private Map<String, Object> paramsObject;
  private Map<String, String> paramsString;

  @Before
  public void setUp() {
    paramsObject = new HashMap<>();
    paramsObject.put(SmsEventParamsConstants.CONFIG, CONFIG);
    paramsObject.put(SmsEventParamsConstants.RECIPIENTS, RECIPIENTS_LIST);
    paramsObject.put(SmsEventParamsConstants.MESSAGE, MESSAGE);
    paramsObject.put(SmsEventParamsConstants.OPENMRS_ID, OPENMRS_ID);
    paramsObject.put(SmsEventParamsConstants.PROVIDER_MESSAGE_ID, PROVIDER_MESSAGE_ID);
    paramsObject.put(SmsEventParamsConstants.FAILURE_COUNT, FAILURE_COUNT);
    paramsObject.put(SmsEventParamsConstants.DELIVERY_TIME, DELIVERY_TIME);
    paramsObject.put(SmsEventParamsConstants.CUSTOM_PARAMS, CUSTOM_PARAMS);

    paramsString = new HashMap<>();
    paramsString.put(SmsEventParamsConstants.CONFIG, CONFIG);
    paramsString.put(SmsEventParamsConstants.RECIPIENTS, RECIPIENTS_LIST_AS_STRING);
    paramsString.put(SmsEventParamsConstants.MESSAGE, MESSAGE);
    paramsString.put(SmsEventParamsConstants.OPENMRS_ID, OPENMRS_ID);
    paramsString.put(SmsEventParamsConstants.PROVIDER_MESSAGE_ID, PROVIDER_MESSAGE_ID);
    paramsString.put(SmsEventParamsConstants.FAILURE_COUNT, FAILURE_COUNT.toString());
    paramsString.put(SmsEventParamsConstants.DELIVERY_TIME, DELIVERY_TIME_AS_STRING);
    paramsString.put(SmsEventParamsConstants.CUSTOM_PARAMS, CUSTOM_PARAMS_AS_STRING);
  }

  @Test
  public void shouldProperlyParsePropertiesToMapStringString() {
    SmsEvent smsEvent = new SmsEvent(paramsObject);
    Map<String, String> actual = smsEvent.convertProperties();

    assertThat(actual.get(SmsEventParamsConstants.CONFIG), equalTo(CONFIG));
    assertThat(actual.get(SmsEventParamsConstants.RECIPIENTS), equalTo(RECIPIENTS_LIST_AS_STRING));
    assertThat(actual.get(SmsEventParamsConstants.MESSAGE), equalTo(MESSAGE));
    assertThat(actual.get(SmsEventParamsConstants.OPENMRS_ID), equalTo(OPENMRS_ID));
    assertThat(
        actual.get(SmsEventParamsConstants.PROVIDER_MESSAGE_ID), equalTo(PROVIDER_MESSAGE_ID));
    assertThat(
        actual.get(SmsEventParamsConstants.FAILURE_COUNT), equalTo(FAILURE_COUNT.toString()));
    assertThat(actual.get(SmsEventParamsConstants.DELIVERY_TIME), equalTo(DELIVERY_TIME_AS_STRING));
    assertThat(actual.get(SmsEventParamsConstants.CUSTOM_PARAMS), equalTo(CUSTOM_PARAMS_AS_STRING));
  }

  @Test
  public void shouldProperlyParsePropertiesToMapStringObject() {
    Map<String, Object> actual = SmsEvent.convertProperties(paramsString);

    assertThat(actual.get(SmsEventParamsConstants.CONFIG), equalTo(CONFIG));
    assertThat(actual.get(SmsEventParamsConstants.RECIPIENTS), equalTo(RECIPIENTS_LIST));
    assertThat(actual.get(SmsEventParamsConstants.MESSAGE), equalTo(MESSAGE));
    assertThat(actual.get(SmsEventParamsConstants.OPENMRS_ID), equalTo(OPENMRS_ID));
    assertThat(
        actual.get(SmsEventParamsConstants.PROVIDER_MESSAGE_ID), equalTo(PROVIDER_MESSAGE_ID));
    assertThat(actual.get(SmsEventParamsConstants.FAILURE_COUNT), equalTo(FAILURE_COUNT));
    assertThat(actual.get(SmsEventParamsConstants.DELIVERY_TIME), equalTo(DELIVERY_TIME));
    assertThat(actual.get(SmsEventParamsConstants.CUSTOM_PARAMS), equalTo(CUSTOM_PARAMS));
  }
}
