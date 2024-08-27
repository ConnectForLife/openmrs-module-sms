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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.util.SMSConstants;
import org.connectforlife.module.sms.api.util.SmsEventParamsConstants;
import org.connectforlife.module.sms.api.util.SmsTaskUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsEvent {
    private static final String RECIPIENTS_DELIMITER = ",";
    private static final String CUSTOM_PARAMS_DELIMITER = ",";
    private static final String CUSTOM_PARAMS_KEY_VALUE_SEPARATOR = "=";

    private String subject;
    private Map<String, Object> parameters;

    public SmsEvent(String subject, Map<String, Object> parameters) {
        this.subject = subject;
        this.parameters = parameters;
    }

    public SmsEvent(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getJobId() {
        return (String) getParameters().get(SMSConstants.PARAM_JOB_ID);
    }

    public String generateTaskName() {
        return SmsTaskUtil.generateTaskName(getSubject(), getOpenMrsId());
    }

    public boolean paramsContainKey(String key) {
        return getParameters().containsKey(key);
    }

    public Integer getFailureCount() {
        return (Integer) getParameters().get(SmsEventParamsConstants.FAILURE_COUNT);
    }

    public String getOpenMrsId() {
        return (String) getParameters().get(SmsEventParamsConstants.OPENMRS_ID);
    }

    public String getProviderId() {
        return (String) getParameters().get(SmsEventParamsConstants.PROVIDER_MESSAGE_ID);
    }

    public String getConfigParam() {
        return (String) getParameters().get(SmsEventParamsConstants.CONFIG);
    }

    public Map<String, Object> getCustomParams() {
        return (Map<String, Object>) getParameters().get(SmsEventParamsConstants.CUSTOM_PARAMS);
    }

    public List<String> getRecipients() {
        return (List<String>) getParameters().get(SmsEventParamsConstants.RECIPIENTS);
    }

    public String getMessage() {
        return (String) getParameters().get(SmsEventParamsConstants.MESSAGE);
    }

    public Date getDeliveryTime() {
        return (Date) getParameters().get(SmsEventParamsConstants.DELIVERY_TIME);
    }

    public Map<String, String> convertProperties() {
        Map<String, String> result = new HashMap<>();

        for (String key : getParameters().keySet()) {
            if (SmsEventParamsConstants.CUSTOM_PARAMS.equals(key)) {
                result.put(key, Joiner.on(CUSTOM_PARAMS_DELIMITER)
                        .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
                        .join(getCustomParams()));
            } else if (SmsEventParamsConstants.RECIPIENTS.equals(key)) {
                List<String> recipients = getRecipients();
                result.put(key, String.join(RECIPIENTS_DELIMITER, recipients));
            } else if (getParameters().get(key) instanceof Date) {
                result.put(key, DateUtil.dateToString((Date) getParameters().get(key)));
            } else {
                result.put(key, getParameters().get(key).toString());
            }
        }

        return result;
    }

    public static Map<String, Object> convertProperties(Map<String, String> properties) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, String> map : properties.entrySet()) {
            String key=map.getKey();
            if (SmsEventParamsConstants.CUSTOM_PARAMS.equals(key)) {
                result.put(key, Splitter.on(CUSTOM_PARAMS_DELIMITER)
                        .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
                        .split(properties.get(SmsEventParamsConstants.CUSTOM_PARAMS)));
            } else if (SmsEventParamsConstants.RECIPIENTS.equals(key)) {
                String recipients = properties.get(SmsEventParamsConstants.RECIPIENTS);
                result.put(key, Arrays.asList(recipients.split(RECIPIENTS_DELIMITER)));
            } else if (SmsEventParamsConstants.DELIVERY_TIME.equals(key)) {
                result.put(key, DateUtil.parse(properties.get(SmsEventParamsConstants.DELIVERY_TIME)));
            } else if (SmsEventParamsConstants.FAILURE_COUNT.equals(key)) {
                result.put(key, Integer.valueOf(properties.get(SmsEventParamsConstants.FAILURE_COUNT)));
            } else {
                result.put(key, properties.get(key));
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
