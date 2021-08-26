/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.tool.Bytes;

/**
 * <p>
 * Created by August.Zhou on 2020/12/15 17:13.
 */
public class LocalCacheEvictMessage {
    private Object key;
    private String name;

    public LocalCacheEvictMessage() {
    }

    public LocalCacheEvictMessage(Object key, String name) {
        this.key = key;
        this.name = name;
    }

    public byte[] toBytes() {
        return Bytes.toBytes(JSON.toJSONString(this));
    }

    public static LocalCacheEvictMessage fromBytes(byte[] bytes) {
        return JSON.parse(Bytes.toString(bytes), LocalCacheEvictMessage.class);
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
