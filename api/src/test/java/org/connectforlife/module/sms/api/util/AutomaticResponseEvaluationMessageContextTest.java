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

import org.connectforlife.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.connectforlife.module.sms.api.configs.Config;
import org.connectforlife.module.sms.api.handler.IncomingMessageData;
import org.connectforlife.module.sms.api.handler.IncomingMessageDataBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

public class AutomaticResponseEvaluationMessageContextTest {

	private AutomaticResponseEvaluationMessageContext automaticResponseEvaluationMessageContext ;

	private final String CONFIG_NAME = "nexmo";

	private final String MESSAGE = "TEST_DATA";

	private final String SENDER = "22210212";

	private IncomingMessageData incomingMessageData;

	@Before
	public void setup()
	{
		Config config = new Config();
		config.setName(CONFIG_NAME);

		incomingMessageData = new IncomingMessageDataBuilder()
														.setConfig(config)
														.setMessage(MESSAGE)
														.setProviderMessageId("TEST_ID")
														.setReceivedAt(DateUtil.now())
														.setReceivedForAFistTime(Boolean.TRUE)
														.setDeliveryStatus(DeliveryStatusesConstants.SCHEDULED)
														.setSender(SENDER)
														.build();
		automaticResponseEvaluationMessageContext = new AutomaticResponseEvaluationMessageContext(incomingMessageData);
	}

	@Test
	public void assertAutomaticResponseEvaluationMessageContext()
	{
		assertThat(incomingMessageData.getConfig(),is(automaticResponseEvaluationMessageContext.getConfig()));
		assertThat(incomingMessageData.getMessage(),is(automaticResponseEvaluationMessageContext.getMessage()));
		assertThat(incomingMessageData.getProviderMessageId(),is(automaticResponseEvaluationMessageContext.getProviderMessageId()));
		assertThat(incomingMessageData.getReceivedAt(),is(automaticResponseEvaluationMessageContext.getReceivedAt()));
		assertThat(incomingMessageData.getSenderPhoneNumber(),is(automaticResponseEvaluationMessageContext.getSenderPhoneNumber()));
		assertThat(incomingMessageData.getDeliveryStatus(),is(automaticResponseEvaluationMessageContext.getDeliveryStatus()));
		assertThat(incomingMessageData.getSenderPhoneNumber(), is(automaticResponseEvaluationMessageContext.getSenderPhoneNumber()));
	}
}
