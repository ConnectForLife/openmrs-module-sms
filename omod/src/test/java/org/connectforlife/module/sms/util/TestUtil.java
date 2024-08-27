/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public final class TestUtil {

    public static final String APPLICATION_JSON = "application/json";

    public static final String UTF_8 = "UTF-8";

    public static byte[] encodeString() throws Exception {
        return getBodyParamsJsonAsString().getBytes(UTF_8);
    }

    private static String getBodyParamsJsonAsString() throws Exception {
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("message_uuid", "7f8c2803-687e-4364-91b205253e0527fd6");
        bodyParams.put("status", "delivered");
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(bodyParams);
    }
}
