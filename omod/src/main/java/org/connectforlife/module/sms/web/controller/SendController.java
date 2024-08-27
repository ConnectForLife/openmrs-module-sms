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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.service.OutgoingSms;
import org.connectforlife.module.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * handles requests to {server}/openmrs/ws/sms/send: how the Send SMS dialog sends a message
 */
@Api(value = "Outgoing requests", tags = {"REST API to handle outgoing requests"})
@Controller
@RequestMapping(value = "/sms")
public class SendController extends RestController {

    private static final Log LOGGER = LogFactory.getLog(SendController.class);

    @Autowired
    @Qualifier("sms.SmsService")
    private SmsService smsService;

    /**
     * Sends an outgoing sms.
     *
     * @param outgoingSms the definition of the SMS to send
     * @return a message describing that the SMS was sent
     * @see OutgoingSms
     */
    @ApiOperation(value = "Sends an outgoing SMS", notes = "Sends an outgoing SMS")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful sending an outgoing SMS"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to send an outgoing SMS"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in an outgoing SMS"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "There are no SMS configs on this server")})
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String sendSms(@ApiParam(name = "outgoingSms", value = "The definition of the SMS to send")
            @RequestBody OutgoingSms outgoingSms) {
        smsService.send(outgoingSms);
        return String.format("SMS to %s using the %s config was added to the message queue.",
                outgoingSms.getRecipients().toString(), outgoingSms.getConfig());
    }

    /**
     * Handles an exception in the controller. The message of the exception will be returned as the HTTP body.
     *
     * @param e the exception to handle
     * @return the message from the exception, to be treated as the response body
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        LOGGER.error("Error in Send SMS Controller", e);
        return e.getMessage();
    }
}
