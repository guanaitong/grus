/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-04-16 17:23.
 */
public class NestedObjectValidator extends ObjectValidator {

    private Map<Field, ObjectValidator> fieldObjectValidatorMap;


    private NestedObjectValidator(String name, Class<?> clazz, Map<Field, ObjectValidator> fieldObjectValidatorMap) {
        super(name, clazz);
        this.fieldObjectValidatorMap = fieldObjectValidatorMap;
    }

    @Override
    public ValidateResult validate(Object paramValue) {
        if (paramValue == null) {
            return ValidateResult.SUCCESS;
        }
        for (Map.Entry<Field, ObjectValidator> entry : fieldObjectValidatorMap.entrySet()) {
            Field field = entry.getKey();
            Object fieldValue = getFieldValue(field, paramValue);
            ValidateResult validateResult = entry.getValue().validate(fieldValue);
            if (!validateResult.isValid()) {
                return validateResult;
            }
        }
        return ValidateResult.SUCCESS;
    }

    private static Object getFieldValue(Field field, Object object) {
        try {
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectValidator build(String paramName, Class<?> paramClass) {
        if (Object.class.equals(paramClass)) {
            return NoopObjectValidator.INSTANCE;
        }
        Map<Field, ObjectValidator> fieldObjectValidatorMap = new HashMap<>();
        Field[] declaredFields = paramClass.getDeclaredFields();
        NestedObjectValidator nestedObjectValidator = new NestedObjectValidator(paramName, paramClass, fieldObjectValidatorMap);
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Class<?> fieldDeclaringClass = field.getType();
            ObjectValidator objectValidator = SimpleObjectValidator.build(field.getName(), fieldDeclaringClass, field.getAnnotations());
            if (objectValidator == NoopObjectValidator.INSTANCE) {
                if (isArrayType(fieldDeclaringClass)) {
                    objectValidator = new ArrayObjectValidator(field.getName(), fieldDeclaringClass);
                } else if (isIterableType(fieldDeclaringClass)) {
                    objectValidator = new CollectionObjectValidator(field.getName(), fieldDeclaringClass);
                } else if (isMapType(fieldDeclaringClass)) {
                    objectValidator = null;
                } else {
                    if (paramClass.equals(fieldDeclaringClass)) {
                        objectValidator = nestedObjectValidator;
                    } else {
                        objectValidator = build(field.getName(), fieldDeclaringClass);
                    }
                }
            }
            if (objectValidator != null) {
                fieldObjectValidatorMap.put(field, objectValidator);
            }
        }
        if (fieldObjectValidatorMap.size() == 0) {
            return NoopObjectValidator.INSTANCE;
        }
        return nestedObjectValidator;
    }
}
