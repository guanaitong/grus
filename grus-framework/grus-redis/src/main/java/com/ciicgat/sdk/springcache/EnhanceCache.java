/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * Created by August.Zhou on 2023/10/17 21:22
 */
public interface EnhanceCache extends Cache {
    ValueWrapper getByStringKey(String key);

    void putByStringKey(String key, Object value);

    void evictByStringKey(String key);

    /**
     * 将kv存缓存，相对于Cache.put方法的区别是：
     * 该方法会保证数据在所有缓存介质中是最新的，主要用于解决本地缓存put时，只更新当前内存的情况。
     * 对于redis，两个方法没有区别
     *
     * @param key
     * @param value
     */
    void putNewValue(String key, Object value);

    <T> T getWithCacheFallBack(Object key, Callable<T> valueLoader);

}
