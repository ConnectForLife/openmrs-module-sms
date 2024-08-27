/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.task;

import org.connectforlife.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.tasks.AbstractTask;

public abstract class AbstractSmsTask extends AbstractTask {

    @Override
    public void execute() {
        try {
            executeTask();
        } finally {
            shutdownTask();
        }
    }

    protected abstract void executeTask();

    private void shutdownTask() {
        try {
            Context.getSchedulerService().shutdownTask(getTaskDefinition());
        } catch (SchedulerException ex) {
            throw new SmsRuntimeException(ex);
        }
    }
}
