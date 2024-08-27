/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.web;

import org.apache.commons.lang.StringUtils;
import org.connectforlife.module.sms.api.util.DateUtil;
import org.connectforlife.module.sms.api.web.dto.BaseDTO;

import java.util.Date;

public class Interval extends BaseDTO {

	private static final long serialVersionUID = 7249387385043145349L;
	private Date from;

	private Date to;

	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public Interval(String timeFrom, String timeTo) {
		if (StringUtils.isNotBlank(timeFrom)) {
			from = DateUtil.parse(timeFrom, DATE_TIME_PATTERN);
		} else {
			from = new Date(0);
		}
		if (StringUtils.isNotBlank(timeTo)) {
			to = DateUtil.parse(timeTo, DATE_TIME_PATTERN);
		} else {
			to = DateUtil.now();
		}
	}

	public Interval(Date from, Date to) {
		this.from = from!= null ? new Date(from.getTime()) : null;
		this.to = to!= null ? new Date(to.getTime()) : null;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from!= null ? new Date(from.getTime()) : null;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to!= null ? new Date(to.getTime()) : null;
	}
}
