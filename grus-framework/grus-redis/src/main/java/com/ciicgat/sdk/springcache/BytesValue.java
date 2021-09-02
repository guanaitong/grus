/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

/**
 * Created by August.Zhou on 2021/9/2 16:35.
 */
class BytesValue {
    private final byte[] bytes;
    private final Object value;

    BytesValue(byte[] bytes, Object value) {
        this.bytes = bytes;
        this.value = value;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Object getValue() {
        return value;
    }
}
