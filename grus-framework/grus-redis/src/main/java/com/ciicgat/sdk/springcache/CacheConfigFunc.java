/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

/**
 * Created by August.Zhou on 2019-08-21 11:36.
 */
@FunctionalInterface
public interface CacheConfigFunc {
    CacheConfigFunc DEFAULT = cacheName -> CacheConfig.redis();

    CacheConfig getCacheConfig(String cacheName);
}
