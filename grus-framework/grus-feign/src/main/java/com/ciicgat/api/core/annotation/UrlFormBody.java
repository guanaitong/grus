/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记一个类是否可以转化为urlform的形式
 * Created by August.Zhou on 2017/8/2 10:12.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface UrlFormBody {
}
