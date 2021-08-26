/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.useragent;

import java.io.Serializable;

/**
 * Created by August.Zhou on 2017/6/23 10:52.
 */
@Deprecated
public enum LoginEntry implements Serializable {
    PC(1),
    MOBILE(2),
    WEIXIN(3),
    APP(4),
    WEIXIN_WORK(5);

    private int value;

    LoginEntry(int value) {
        this.value = value;
    }

    public static LoginEntry valueOf(int value) {
        for (LoginEntry loginEntry : values()) {
            if (value == loginEntry.value) {
                return loginEntry;
            }
        }
        throw new RuntimeException();
    }

    public int getValue() {
        return value;
    }

}
