/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: chunhong.wan
 * @Date: 2020/12/10
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelColumn {
    /**
     * 对应excel中的列，从0开始
     * @return
     */
    int column();

    /**
     * 对应excel中的首行的名称
     * @return
     */
    String name();

    /**
     * converter pattern
     * 日期类型需提供,否则按照 yyyy-MM-dd HH:mm:ss
     * @example "yyyy-MM-dd"
     */
    String pattern() default "";

}
