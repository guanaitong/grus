/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;


import com.ciicgat.sdk.lang.exception.CacheDataException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2017/1/3 17:31.
 */
public class LocalCache {
    private final Cache<Object, Object> localCache;
    private static final Object NULL = new Object();

    public LocalCache(int expiredSecond) {
        this(expiredSecond, 409600L);
    }

    public LocalCache(int expiredSecond, long maximumSize) {
        this.localCache = Caffeine.newBuilder()
                .expireAfterWrite(expiredSecond, TimeUnit.SECONDS)
                .maximumSize(maximumSize)
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Object key) throws CacheDataException {
        Object value = localCache.getIfPresent(key);
        return value == NULL ? null : (T) value;
    }

    public <T> void setValue(Object key, T value) throws CacheDataException {
        localCache.put(key, value == null ? NULL : value);
    }

    public void evict(Object key) throws CacheDataException {
        localCache.invalidate(key);
    }
}
