/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

/**
 * Created by Albert on 2017/11/27.
 */
public interface Converter<T> {

    T newOne();

    default T toAnother() {
        T t = newOne();
        //TODO 基于原生JDK 进行copy BeanUtils.copyProperties(t, this);
        return t;
    }

}

