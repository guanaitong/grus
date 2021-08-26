/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import java.util.concurrent.Callable;

/**
 * @Author: August
 * @Date: 2021/4/13 15:22
 */
public interface CacheRefresher {

    CacheRefresher NOOP = new CacheRefresher() {
        @Override
        public void mayRefresh(AbstractCache cache, String key, Callable<Object> valueLoader) {

        }

        @Override
        public void recordCacheInit(AbstractCache cache, String key) {

        }
    };

    /**
     * 在满足条件时，触发缓存的更新。
     *
     * @param cache
     * @param key
     * @param valueLoader 注意：该闭包不能是被代理的方法
     */
    void mayRefresh(AbstractCache cache, String key, Callable<Object> valueLoader);

    /**
     * 在缓存未命中，初始化缓存后，记录下这次事件。
     *
     * @param cache
     * @param key
     */
    void recordCacheInit(AbstractCache cache, String key);

}
