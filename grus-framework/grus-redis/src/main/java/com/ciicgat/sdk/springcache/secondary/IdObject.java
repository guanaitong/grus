/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.secondary;

/**
 * Created by August.Zhou on 2021/12/17 10:13.
 */
public record IdObject<Id, Value>(Id id, Value object) {

    public Id getId() {
        return id;
    }

    public Value getObject() {
        return object;
    }
}
