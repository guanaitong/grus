/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by August.Zhou on 2016/12/27 12:48.
 */
public class PrimaryCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryCache.class);

    private final LocalCache cache;

    public PrimaryCache(LocalCache cache) {
        this.cache = cache;
    }

    public PrimaryCache() {
        this.cache = new LocalCache(3600 * 2);
    }


    /**
     * 获取主缓存
     *
     * @param key         缓存key
     * @param valueLoader 没命中缓存执行
     * @return
     * @throws Exception
     */
    public <T> T get(Object key, final Supplier<T> valueLoader) {
        Objects.requireNonNull(key);

        T object = null;
        try {
            object = cache.getValue(key);
            if (null != object) {
                return object;
            }
        } catch (Throwable e) {
            LOGGER.warn("获取一级缓存异常：", e);
        }

        object = valueLoader.get();
        if (object != null) {
            cache.setValue(key, object);
        }
        return object;
    }


    public void invalidate(Object key) {
        cache.evict(key);
    }


}
