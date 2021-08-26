/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import java.lang.reflect.Array;

/**
 * Created by August.Zhou on 2019-07-03 18:07.
 */
public class ArrayObjectValidator extends ObjectValidator {

    private final Class<?> componentType;

    private ObjectValidator objectValidator;

    public ArrayObjectValidator(String name, Class<?> clazz) {
        super(name, clazz);
        this.componentType = clazz.getComponentType();
        objectValidator = NestedObjectValidator.build(name, this.componentType);
    }

    @Override
    public ValidateResult validate(Object paramValue) {
        if (objectValidator == null || paramValue == null) {
            return ValidateResult.SUCCESS;
        }
        int length = Array.getLength(paramValue);
        for (int i = 0; i < length; i++) {
            Object value = Array.get(paramValue, i);
            ValidateResult validate = objectValidator.validate(value);
            if (!validate.isValid()) {
                return validate;
            }
        }
        return ValidateResult.SUCCESS;
    }
}
