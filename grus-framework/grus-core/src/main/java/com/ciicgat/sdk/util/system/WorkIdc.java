/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

/**
 * Created by August.Zhou on 2017/12/14 9:49.
 */
public enum WorkIdc {
    OFC, JX, SH, ALI;

    private final String innerDomainSuffix;

    WorkIdc() {
        this.innerDomainSuffix = this.name().toLowerCase();
    }

    static WorkIdc getWorkIdc(String name) {
        if (name == null || name.isBlank()) {
            return WorkIdc.OFC;
        }
        name = name.toLowerCase();
        for (WorkIdc workIdc : WorkIdc.values()) {
            if (workIdc.name().toLowerCase().equals(name)) {
                return workIdc;
            }
        }
        return WorkIdc.OFC;
    }

    public final String getInnerDomainSuffix() {
        return innerDomainSuffix;
    }

}
