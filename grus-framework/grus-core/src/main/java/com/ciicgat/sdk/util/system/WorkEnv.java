/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

/**
 * Created by August.Zhou on 2017/12/14 9:49.
 */
public enum WorkEnv {
    DEVELOP("dev", "dev"),
    TEST("test", "test"),
    PRODUCT("product", "product"),
    PREPARE("prepare", "product");

    private final String name;
    private final String innerDomainSuffix;


    WorkEnv(String name, String innerDomainSuffix) {
        this.innerDomainSuffix = innerDomainSuffix;
        this.name = name;
    }

    static WorkEnv getWorkEnv(String name) {
        if (name == null || name.isBlank()) {
            return WorkEnv.DEVELOP;
        }
        name = name.toLowerCase();
        for (WorkEnv workEnv : WorkEnv.values()) {
            if (workEnv.name.toLowerCase().equals(name)) {
                return workEnv;
            }
        }
        return WorkEnv.DEVELOP;
    }

    public final String getInnerDomainSuffix() {
        return innerDomainSuffix;
    }

    public final String getName() {
        return name;
    }


}
