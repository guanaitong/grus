/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by August.Zhou on 2019-04-16 17:03.
 */
public class SimpleObjectValidator extends ObjectValidator {

    private final List<Validator<Annotation>> validators;

    private final List<? extends Annotation> annotations;

    private final int length;

    private SimpleObjectValidator(String name, Class<?> clazz, List<Validator<Annotation>> validators, List<Annotation> annotations) {
        super(name, clazz);
        this.validators = validators;
        this.annotations = annotations;
        length = validators.size();
    }

    @Override
    public ValidateResult validate(Object paramValue) {
        for (int i = 0; i < length; i++) {
            Annotation annotation = annotations.get(i);
            ValidateResult validateResult = validators.get(i).validate(annotation, name, paramValue);
            if (!validateResult.isValid()) {
                return validateResult;
            }
        }
        return ValidateResult.SUCCESS;
    }

    public static ObjectValidator build(String paramName, Class<?> paramClass, Annotation[] annotations) {
        if (annotations == null) {
            return NoopObjectValidator.INSTANCE;
        }
        List<Validator<Annotation>> validatorList = new ArrayList<>();
        List<Annotation> annotationList = new ArrayList<>();
        for (Annotation annotation : annotations) {
            Validator validator = Validators.getValidator(annotation);
            if (validator != null) {
                validatorList.add(validator);
                annotationList.add(annotation);
            }
        }
        if (validatorList.size() == 0) {
            return NoopObjectValidator.INSTANCE;
        }
        return new SimpleObjectValidator(paramName, paramClass, validatorList, annotationList);
    }
}
