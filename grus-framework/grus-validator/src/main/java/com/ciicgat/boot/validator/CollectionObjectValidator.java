/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

/**
 * Created by August.Zhou on 2019-07-03 18:07.
 */
public class CollectionObjectValidator extends ObjectValidator {


    private RootObjectValidator rootObjectValidator;


    public CollectionObjectValidator(String name, Class<?> clazz) {
        super(name, clazz);
        rootObjectValidator = new RootObjectValidator();
    }

    @Override
    public ValidateResult validate(Object paramValue) {
        if (paramValue == null) {
            return ValidateResult.SUCCESS;
        }
        Iterable iterable = (Iterable) paramValue;
        for (Object o : iterable) {
            ValidateResult validate = rootObjectValidator.validate(o);
            if (!validate.isValid()) {
                return validate;
            }
        }
        return ValidateResult.SUCCESS;
    }
}
