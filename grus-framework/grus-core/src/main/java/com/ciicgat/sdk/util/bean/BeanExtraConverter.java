/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/14 19:42
 * @Description:
 */
public interface BeanExtraConverter<T, R> {
    void afterProcess(T src, R dst);
}
