/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.validate;

import org.connectforlife.module.sms.api.exception.ValidationException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValidationComponent {

    private LocalValidatorFactoryBean factory;

    /**
     * Generic method which validates objects according its annotations.
     *
     * @param objectToValidate object to validate
     * @param clazz            class of the object. If not passed automatically inferred from objectToValidate.
     * @throws ValidationException if validation error found
     */
    public <T> void validate(T objectToValidate, Class<?>... clazz) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate, clazz);
        if (!violations.isEmpty()) {
            throw new ValidationException(buildValidationErrorCause(violations));
        }
    }

    public ValidationComponent setFactory(LocalValidatorFactoryBean factory) {
        this.factory = factory;
        return this;
    }

    private static <T> Map<String, String> buildValidationErrorCause(Set<ConstraintViolation<T>> violations) {
        Map<String, String> map = new HashMap<>(violations.size());
        for (ConstraintViolation<T> violation : violations) {
            map.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return map;
    }

}
