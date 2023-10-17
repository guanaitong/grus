/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.event;

import com.ciicgat.sdk.springcache.AbstractCache;

/**
 * Created by August.Zhou on 2021/11/26 13:27.
 */
public class CacheChangeEvent {
    private final CacheChangeType cacheChangeType;
    private final String key;
    private final AbstractCache<?> cache;

    public CacheChangeEvent(CacheChangeType cacheChangeType, String key, AbstractCache<?> cache) {
        this.cacheChangeType = cacheChangeType;
        this.key = key;
        this.cache = cache;
    }

    public AbstractCache<?> getCache() {
        return cache;
    }

    public CacheChangeType getCacheChangeType() {
        return cacheChangeType;
    }

    public String getKey() {
        return key;
    }
}
