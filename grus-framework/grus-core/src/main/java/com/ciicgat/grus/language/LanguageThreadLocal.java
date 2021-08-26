/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.language;

import java.util.Locale;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/03/17 10:33
 * @Description:
 */
public class LanguageThreadLocal {
    private static final ThreadLocal<Locale> LANG_THREAD_LOCAL = new ThreadLocal<>();

    public static void setLocale(Locale locale) {
        LANG_THREAD_LOCAL.set(locale);
    }

    public static Locale getLocale() {
        return LANG_THREAD_LOCAL.get();
    }

    public static void remove() {
        LANG_THREAD_LOCAL.remove();
    }

}
