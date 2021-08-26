/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import com.ciicgat.sdk.lang.tool.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by August.Zhou on 2019-04-10 14:21.
 */
public class Validators {

    private static final Logger LOGGER = LoggerFactory.getLogger(Validators.class);

    private static final ConcurrentHashMap<Class<? extends Annotation>, Validator> VALIDATOR_MAP = new ConcurrentHashMap<>();

    static {
        init();
    }

    private static void init() {
        List<Class<?>> classes = ClassUtils.getClasses("com.ciicgat.boot.validator.runtime");
        for (Class<?> aClass : classes) {
            if (Validator.class.isAssignableFrom(aClass)) {
                try {
                    Validator instance = (Validator) aClass.getDeclaredConstructor().newInstance();
                    VALIDATOR_MAP.put(instance.getType(), instance);
                } catch (Exception e) {
                    LOGGER.warn("init failed for class " + aClass, e);
                }

            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void register(Validator validator) {
        VALIDATOR_MAP.put(validator.getType(), validator);
    }


    public static Validator getValidator(Annotation annotation) {
        if (annotation != null) {
            return getValidator(annotation.annotationType());
        }
        return null;
    }

    public static Validator getValidator(Class<? extends Annotation> annotationType) {
        return VALIDATOR_MAP.get(annotationType);
    }

    public static Set<Class<? extends Annotation>> getValidatorAnnotationSet() {
        return VALIDATOR_MAP.keySet();
    }

    public boolean isValidAnnotation(Class<? extends Annotation> annotationType) {
        return VALIDATOR_MAP.containsKey(annotationType);
    }

    public static boolean hasValidator(Annotation annotation) {
        return VALIDATOR_MAP.containsKey(annotation.annotationType());
    }

}
