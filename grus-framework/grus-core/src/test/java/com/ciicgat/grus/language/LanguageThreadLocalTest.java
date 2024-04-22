/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.language;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**
 * Created by August.Zhou on 2024/4/22 10:14
 */
class LanguageThreadLocalTest {

    @Test
    void setLocale() {
        LanguageThreadLocal.setLocale(Locale.ENGLISH);
        Assertions.assertEquals(Locale.ENGLISH, LanguageThreadLocal.getLocale());
        LanguageThreadLocal.remove();
        Assertions.assertNull(LanguageThreadLocal.getLocale());
    }
}
