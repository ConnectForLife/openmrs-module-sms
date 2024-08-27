/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.connectforlife.module.sms.api.web.ErrorResponse;
import org.openmrs.api.APIAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class RestController {

    private static final Log LOGGER = LogFactory.getLog(RestController.class);
    private static final String ERR_SYSTEM = "system.error";

    /**
     * Exception handler for lack of the adequate permissions - Http status code of 403
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(APIAuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleException(APIAuthenticationException e) {
        LOGGER.error(e.getMessage(), e);
        return new ErrorResponse(ERR_SYSTEM, e.getMessage());
    }

    public Map<String, String> getCombinedParams(Map<String, String> params, Map<String, Object> bodyParam) {
        Map<String, String> paramMap = new HashMap<>();
        if (params != null) {
            paramMap.putAll(params);
        }
        ObjectMapper mapper;
        if (bodyParam != null) {
            for (Map.Entry<String, Object> en : bodyParam.entrySet()) {
                mapper = new ObjectMapper();
                if (en.getValue().getClass().equals(String.class)) {
                    paramMap.put(en.getKey(), en.getValue().toString());
                } else {
                    try {
                        String json = mapper.writeValueAsString(en.getValue());
                        paramMap.put(en.getKey(), json);
                    } catch (IOException e) {
                        throw new SmsRuntimeException("invalid object", e);
                    }
                }
            }
        }
        return paramMap;
    }
}
