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

import org.apache.commons.io.IOUtils;
import org.connectforlife.module.sms.api.exception.SmsRuntimeException;

import java.io.IOException;
import java.io.InputStream;

public final class ResourceUtil {

    public static boolean resourceFileExists(String filename) {
        return ResourceUtil.class.getClassLoader().getResource(filename) != null;
    }

    public static String readResourceFile(String filename) {
        try (InputStream in = ResourceUtil.class.getClassLoader().getResourceAsStream(filename)) {
            if (in == null) {
                throw new SmsRuntimeException("Resource '" + filename + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new SmsRuntimeException(e);
        }
    }

    private ResourceUtil() {
    }

}
