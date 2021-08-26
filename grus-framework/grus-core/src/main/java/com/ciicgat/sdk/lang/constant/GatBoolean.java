/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.constant;

/**
 * 关爱通标准的整形值布尔类
 * Created by Jiaju.Wei on 2017/11/27.
 */
public class GatBoolean {
    public static final Integer TRUE = 1;

    public static final Integer FALSE = 2;

    /**
     * 空值认为非true
     */
    public static boolean isTrue(Integer flag) {
        return TRUE.equals(flag);
    }

    /**
     * 空值认为非false
     */
    public static boolean isFalse(Integer flag) {
        return FALSE.equals(flag);
    }
}
