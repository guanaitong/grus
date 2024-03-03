/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用来标注接口是否需要走https
 * Created by August.Zhou on 2024/4/3 14:19
 */
@Retention(RUNTIME)
@Target({TYPE, PACKAGE})
public @interface ApiSecurity {

}
