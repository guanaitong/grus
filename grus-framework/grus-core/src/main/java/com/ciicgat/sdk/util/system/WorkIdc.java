/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

/**
 * Created by August.Zhou on 2017/12/14 9:49.
 */
public enum WorkIdc {
    OFC, SH, ALI, LYRA;

    private final String name;

    WorkIdc() {
        this.name = this.name().toLowerCase();
    }

    public final String getInnerDomainSuffix() {
        return name;
    }

    public final String getName() {
        return name;
    }
}
