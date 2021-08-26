/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import com.google.common.primitives.Primitives;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-04-16 16:53.
 *
 * @deprecated use hibernate validation instead
 */
@Deprecated
public abstract class ObjectValidator {

    protected final String name;

    protected final Class<?> clazz;

    public ObjectValidator(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public abstract ValidateResult validate(Object paramValue);

    static boolean isSimpleType(Class<?> clazz) {
        return Primitives.allPrimitiveTypes().contains(clazz)
                || Primitives.allWrapperTypes().contains(clazz)
                || clazz == String.class
                || clazz == Character.class
                || clazz == BigDecimal.class;
    }

    static boolean isArrayType(Class<?> clazz) {
        return clazz.isArray();
    }

    static boolean isIterableType(Class<?> clazz) {
        return Iterable.class.isAssignableFrom(clazz);
    }

    static boolean isMapType(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

}
