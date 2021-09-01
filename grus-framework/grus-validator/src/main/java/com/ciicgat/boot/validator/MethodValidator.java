/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by August.Zhou on 2019-04-16 16:39.
 */
public class MethodValidator {


    private final Method method;
    private final Class<?>[] paramTypes;
    private final String[] paramNames;


    private IndexObjectValidator[] indexObjectValidators;

    private final int length;

    public MethodValidator(Method method, String[] paramNames) {
        this.method = method;
        this.paramTypes = method.getParameterTypes();
        this.paramNames = paramNames;
        this.length = paramTypes.length;
        init();
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public ValidateResult validate(Object[] paramValues) {
        if (indexObjectValidators.length == 0) {
            return ValidateResult.SUCCESS;
        }
        for (IndexObjectValidator indexObjectValidator : indexObjectValidators) {
            ValidateResult validateResult = indexObjectValidator.getObjectValidator().validate(paramValues[indexObjectValidator.getIndex()]);
            if (!validateResult.isValid()) {
                return validateResult;
            }
        }
        return ValidateResult.SUCCESS;
    }

    private void init() {
        List<IndexObjectValidator> indexObjectValidatorList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Class<?> paramClass = paramTypes[i];
            String paramName = paramNames[i];
            ObjectValidator objectValidator = SimpleObjectValidator.build(paramName, paramClass, method.getParameterAnnotations()[i]);
            if (objectValidator == NoopObjectValidator.INSTANCE) {
                objectValidator = NestedObjectValidator.build(paramName, paramClass);
            }
            if (objectValidator == null) {
                objectValidator = NoopObjectValidator.INSTANCE;
            }
            indexObjectValidatorList.add(new IndexObjectValidator(objectValidator, i));
        }
        indexObjectValidators = indexObjectValidatorList.toArray(new IndexObjectValidator[0]);
    }

}
