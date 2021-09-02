/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 为了方便对环境值的hook或者自定义
 * Created by August.Zhou on 2019-02-22 12:54.
 */
public class EnvHook {

    private static String appName = null;


    public static void setAppName(String appName) {
        EnvHook.appName = appName;
    }

    static String getAppName(final Supplier<String> supplier) {
        if (Objects.nonNull(EnvHook.appName)) {
            return EnvHook.appName;
        }
        return supplier.get();
    }
}
