/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.secondary;

/**
 * Created by August.Zhou on 2021/12/17 10:13.
 */
public class IdObject<Id, Value> {
    private final Id id;
    private final Value object;

    public IdObject(Id id, Value object) {
        this.id = id;
        this.object = object;
    }

    public Id getId() {
        return id;
    }

    public Value getObject() {
        return object;
    }
}
