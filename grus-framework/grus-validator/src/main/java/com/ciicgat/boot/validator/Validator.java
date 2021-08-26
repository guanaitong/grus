/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Josh on 17-6-10.
 *
 * @deprecated use hibernate validation instead
 */
@Deprecated
public abstract class Validator<T extends Annotation> {


    private final Class<T> type;


    @SuppressWarnings("unchecked")
    protected Validator() {
        Type superClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        this.type = (Class<T>) type;
    }

    public final Class<T> getType() {
        return type;
    }

    public abstract ValidateResult validate(T t, String paramName, Object paramValue);


}
