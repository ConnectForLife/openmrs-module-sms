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
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/sms/smsLink.form'.
 */
@Controller("${rootrootArtifactId}.SmsController")
@RequestMapping(value = "module/sms/sms.form")
public class SmsController extends RestController {

  /** Logger for this class and subclasses */
  private static final Log LOG = LogFactory.getLog(SmsController.class);

  @Autowired
  @Qualifier("userService")
  private UserService userService;

  /** Success form view name */
  private static final String VIEW = "/module/sms/sms";

  /**
   * Initially called after the getUsers method to get the landing form name
   *
   * @return String form view name
   */
  @RequestMapping(method = RequestMethod.GET)
  public String onGet() {
    return VIEW;
  }

  /**
   * All the parameters are optional based on the necessity
   *
   * @param httpSession
   * @param anyRequestObject
   * @param errors
   * @return
   */
  @RequestMapping(method = RequestMethod.POST)
  public String onPost(
      HttpSession httpSession,
      @ModelAttribute("anyRequestObject") Object anyRequestObject,
      BindingResult errors) {

    if (errors.hasErrors()) {
      LOG.debug(errors);
      // return error view
    }

    return VIEW;
  }

  /**
   * This class returns the form backing object. This can be a string, a boolean, or a normal java
   * pojo. The bean name defined in the ModelAttribute annotation and the type can be just defined
   * by the return type of this method
   */
  @ModelAttribute("users")
  protected List<User> getUsers() {
    // this object will be made available to the jsp page under the variable name
    // that is defined in the @ModuleAttribute tag
    return userService.getAllUsers();
  }
}
