/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.templates;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class IncomingTest {

    private static final String EXPECTED_PHONE = "316511515125";
    private static final String VALID_RECIPIENT_MESSAGE = "recipient: " + EXPECTED_PHONE;
    private static final String INVALID_RECIPIENT_MESSAGE = EXPECTED_PHONE;
    private static final String RECIPIENT_REGEX = "recipient: (.*)";
    private static final String SENDER_REGEX = "sender: (.*)";
    private static final String EXPECTED_SENDER = "215661333";
    private static final String VALID_SENDER_MESSAGE = "sender: " + EXPECTED_SENDER;
    private static final String INVALID_SENDER_MESSAGE = EXPECTED_SENDER;

    @Test
    public void extractRecipientShouldReturnExpectedResult() {
        Incoming incoming = new Incoming();
        incoming.setRecipientRegex(RECIPIENT_REGEX);
        String actual = incoming.extractRecipient(VALID_RECIPIENT_MESSAGE);
        assertThat(actual, is(EXPECTED_PHONE));
    }

    @Test
    public void extractRecipientShouldReturnNullWhenInvalidMessage() {
        Incoming incoming = new Incoming();
        incoming.setRecipientRegex(RECIPIENT_REGEX);
        String actual = incoming.extractRecipient(INVALID_RECIPIENT_MESSAGE);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void extractSenderShouldReturnExpectedResult() {
        Incoming incoming = new Incoming();
        incoming.setSenderRegex(SENDER_REGEX);
        String actual = incoming.extractSender(VALID_SENDER_MESSAGE);
        assertThat(actual, is(EXPECTED_SENDER));
    }

    @Test
    public void extractSenderShouldReturnNullWhenInvalidMessage() {
        Incoming incoming = new Incoming();
        incoming.setSenderRegex(SENDER_REGEX);
        String actual = incoming.extractSender(INVALID_SENDER_MESSAGE);
        assertThat(actual, is(nullValue()));
    }
}
