/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

/**
 * 表示一个cache是否可以过期
 *
 * @Author: August
 * @Date: 2021/4/13 14:20
 */
public interface IRedisCache {

    /**
     * 返回key的剩余过期时间，单位秒
     *
     * @param key
     * @return
     */
    long ttl(Object key);
}
