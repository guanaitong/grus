/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by August.Zhou on 2019-02-22 13:39.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface GConfBean {

    /**
     * Gconf中的应用名，可以不填，默认为当前系统自动识别的
     */
    String appId() default "";

    /**
     * Gconf中的key
     */
    @AliasFor("value")
    String key() default "";

    @AliasFor("key")
    String value() default "";

    boolean autoRefresh() default true;


}

