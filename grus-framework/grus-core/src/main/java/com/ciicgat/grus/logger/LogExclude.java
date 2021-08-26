/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.logger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Stanley Shen
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogExclude {

    /**
     * 隐藏入参的日志
     *
     * @return true or false
     */
    boolean excludeReq() default true;

    /**
     * 隐藏返回的日志
     *
     * @return true or false
     */
    boolean excludeResp() default true;

}
