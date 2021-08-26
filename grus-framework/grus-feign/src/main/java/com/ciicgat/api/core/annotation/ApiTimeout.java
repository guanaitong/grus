/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.ciicgat.api.core.contants.TimeOutConstants.DEFAULT_CONNECT_TIMEOUT_MILLIS;
import static com.ciicgat.api.core.contants.TimeOutConstants.DEFAULT_READ_TIMEOUT_MILLIS;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于指定服务的超时。相对于FeignServiceFactory工厂方法设置的超时，ApiTimeout的优先级低。如果两者都设置了，FeignServiceFactory的生效
 * 对于本身作用，方法>类>包
 * Created by August.Zhou on 2018-10-09 16:08.
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE, PACKAGE})
public @interface ApiTimeout {

    int connectTimeoutMillis() default DEFAULT_CONNECT_TIMEOUT_MILLIS;

    int readTimeoutMillis() default DEFAULT_READ_TIMEOUT_MILLIS;

}
