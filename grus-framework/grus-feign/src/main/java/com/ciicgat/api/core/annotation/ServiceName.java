/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by August.Zhou on 2017/7/28 12:45.
 */

@Retention(RUNTIME)
@Target({PACKAGE, TYPE})
public @interface ServiceName {

    /**
     * serviceName值
     *
     * @return
     */
    String value();

    /**
     * url path的前缀。适合在服务有一个统一前缀path时设置。
     *
     * @return
     */
    String urlPathPrefix() default "";
}
