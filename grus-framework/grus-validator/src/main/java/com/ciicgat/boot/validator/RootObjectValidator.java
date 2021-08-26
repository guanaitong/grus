/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import com.ciicgat.boot.validator.exception.ValidateRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by August.Zhou on 2019-07-03 15:34.
 */
public class RootObjectValidator extends ObjectValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootObjectValidator.class);

    private ConcurrentMap<Class<?>, ObjectValidator> validatorConcurrentMap = new ConcurrentHashMap<>();
    private int errorCode;

    public RootObjectValidator() {
        super("root", Object.class);
    }

    public RootObjectValidator(int errorCode) {
        super("root", Object.class);
        this.errorCode = errorCode;
    }

    @Override
    public ValidateResult validate(Object paramValue) {
        if (paramValue == null) {
            throw new IllegalArgumentException("对象不能为空");
        }
        Class<?> paramClass = paramValue.getClass();
        ObjectValidator objectValidator = validatorConcurrentMap.get(paramClass);
        if (objectValidator == null) {

            ObjectValidator newObjectValidator;
            if (ObjectValidator.isSimpleType(paramClass)) {
                LOGGER.warn("对象不能为简单类型 {}", paramValue);
                newObjectValidator = NoopObjectValidator.INSTANCE;
            } else {
                newObjectValidator = NestedObjectValidator.build("参数", paramClass);
                if (newObjectValidator == null) {
                    newObjectValidator = NoopObjectValidator.INSTANCE;
                }
            }

            objectValidator = validatorConcurrentMap.putIfAbsent(paramClass, newObjectValidator);
            if (objectValidator == null) {
                objectValidator = newObjectValidator;
            }
        }
        return objectValidator.validate(paramValue);
    }

    public void validateBean(Object paramValue) {
        ValidateResult validateResult = this.validate(paramValue);
        if (!validateResult.isValid()) {
            throw new ValidateRuntimeException(errorCode, validateResult.getFailedReason());
        }
    }
}
