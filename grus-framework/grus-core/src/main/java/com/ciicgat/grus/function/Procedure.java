/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.function;

/**
 * 函数式接口，执行一道手续<strong>手续</strong>，无入参和返回
 *
 * @author Stanley Shen
 * @date 2020/11/15 22:06
 */
@FunctionalInterface
public interface Procedure {

    /**
     * 执行一道手续
     */
    void run();

}
