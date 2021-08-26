/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

/**
 * Created by August.Zhou on 2019-07-04 17:38.
 */
public class NoopObjectValidator extends ObjectValidator {

    public static final NoopObjectValidator INSTANCE = new NoopObjectValidator();

    private NoopObjectValidator() {
        super("noop", Object.class);
    }

    @Override
    public ValidateResult validate(Object paramValue) {
        return ValidateResult.SUCCESS;
    }
}
