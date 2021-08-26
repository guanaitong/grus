/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 调用忽略错误
 * Created by Jiaju.wei on 2018/8/13 14:22.
 *
 * @memo 调用时发生错误或其他异常，当返回为非基础对象时以空值返回，否则返回基础类型的默认值
 * 使用方需要对接口返回的空值或默认值做正确的判断和处理
 * 适用于聚合类接口，其中部分接口失败不影响整体返回
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE, PACKAGE})
public @interface IgnoreError {

}
