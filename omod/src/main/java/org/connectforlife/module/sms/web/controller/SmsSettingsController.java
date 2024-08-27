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
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.configs.Configs;
import org.connectforlife.module.sms.api.exception.ValidationException;
import org.connectforlife.module.sms.api.service.SmsSettingsService;
import org.connectforlife.module.sms.api.templates.TemplateForWeb;
import org.connectforlife.module.sms.api.validate.ValidationComponent;
import org.connectforlife.module.sms.api.web.ErrorResponse;
import org.connectforlife.module.sms.api.web.ValidationErrorResponse;
import org.connectforlife.module.sms.web.dto.ConfigsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Map;

//todo: find a way to report useful information if encountering malformed templates?

/**
 * Sends templates to the UI, sends and receives configs to/from the UI.
 */
@Api(value = "Sends and receives templates/configs to/from the UI",
        tags = {"REST API to send and receive templates/configs to/from the UI"})
@Controller
@RequestMapping(value = "/sms")
public class SmsSettingsController extends RestController {

    private static final Log LOGGER = LogFactory.getLog(SendController.class);
    private static final String ERR_BAD_PARAM = "system.param";
    private static final String VALIDATION_ERROR_OCCURS = "Check the form and send it again.";

    @Autowired
    @Qualifier("sms.SettingsService")
    private SmsSettingsService smsSettingsService;

    @Autowired
    @Qualifier("sms.validationComponent")
    private ValidationComponent validationComponent;

    /**
     * Returns all the templates for the UI.
     *
     * @return a map of templates, keys are template names
     */
    @ApiOperation(value = "Returns all the templates for the UI", notes = "Returns all the templates for the UI")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful returning all the templates"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to return all the templates"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in returning all the templates")})
    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, TemplateForWeb> getTemplates() {
        return smsSettingsService.getTemplates();
    }

    /**
     * Imports templates from the uploaded file.
     *
     * @param file the file containing the templates
     * @throws IOException if there was a problem reading the file
     */
    @ApiOperation(value = "Imports templates from the uploaded file", notes = "Imports templates from the uploaded file")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful importing templates"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to import templates"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in importing templates")})
    @RequestMapping(value = "/templates/import", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void importTemplates(@ApiParam(name = "file", value = "the file containing the templates", required = true)
            @RequestParam(value = "file") MultipartFile file)
            throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(file.getInputStream(), writer);

        smsSettingsService.importTemplates(writer.toString());
    }

    /**
     * Retrieves all configurations for the UI.
     *
     * @return all configurations in the system
     */
    @ApiOperation(value = "Retrieves all configurations for the UI", notes = "Retrieves all configurations for the UI")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful retrieving all configurations"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to retrieve all configurations"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in retrieving all configurations")})
    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public ConfigsDTO getConfigs() {
        return new ConfigsDTO(smsSettingsService.getConfigs());
    }

    /**
     * Saves the provided configurations, overriding old ones.
     *
     * @param configs all configurations to save
     * @return the newly saved configurations
     */
    @ApiOperation(value = "Saves the provided configurations, overriding old ones",
            notes = "Saves the provided configurations, overriding old ones")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful saving provided configurations"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to save provided configurations"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in saving provided configurations")})
    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ConfigsDTO setConfigs(@ApiParam(name = "configs", value = "All configurations to save")
            @RequestBody ConfigsDTO configsDTO) {
        final Configs configs = configsDTO.newConfigs();
        validationComponent.validate(configs);
        return new ConfigsDTO(smsSettingsService.setConfigs(configs));
    }

    /**
     * Handles exceptions, returns their message as the response body.
     *
     * @param e the exception to handle
     * @return the exception message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) {
        LOGGER.error("Error in SMS SettingsController", e);
        return e.getMessage();
    }

    @ApiOperation(value = "Fetch custom UI settings", notes = "Fetch custom UI settings")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful fetching custom UI settings"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to fetch custom UI settings"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in fetching custom UI settings")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/mds-databrowser-config", method = RequestMethod.GET)
    @ResponseBody
    public String getCustomUISettings() {
        return smsSettingsService.getCustomUISettings();
    }

    /**
     * Exception handler for validation bad request - Http status code of 400
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleException(ValidationException e) {
        return new ValidationErrorResponse(ERR_BAD_PARAM, VALIDATION_ERROR_OCCURS, e.getConstraintViolations());
    }
}
