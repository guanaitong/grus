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


    private ObjectValidator objectValidator;

    public ArrayObjectValidator(String name, Class<?> clazz) {
        super(name, clazz);
        objectValidator = NestedObjectValidator.build(name, clazz.getComponentType());
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
