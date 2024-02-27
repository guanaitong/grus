/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
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

    public final String getInnerDomainSuffix() {
        return innerDomainSuffix;
    }

    public final String getName() {
        return name;
    }


}
