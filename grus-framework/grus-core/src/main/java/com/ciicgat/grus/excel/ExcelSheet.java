/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExcelSheet {

    /**
     * xls或xlsx
     */
    ExcelType type();

    /**
     * sheet读取序号，较名称优先
     */
    int readIndex() default -1;

    /**
     * 表单sheet名称
     */
    String name() default "";
}
