/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 为了方便对环境值的hook或者自定义
 * Created by August.Zhou on 2019-02-22 12:54.
 */
public class EnvPrepare {

    private static final Map<String, String> PREPARES = new Hashtable<>();


    public static void put(String key, String value) {
        PREPARES.put(key, value);
    }

    static String get(final String key, final Supplier<String> supplier) {
        String v = PREPARES.get(key);
        if (v != null && !v.isBlank()) {
            return v;
        }
        return supplier.get();
    }
}
