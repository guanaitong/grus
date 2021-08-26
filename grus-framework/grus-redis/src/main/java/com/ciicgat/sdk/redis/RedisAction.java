/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import redis.clients.jedis.Jedis;

import java.util.function.Function;

/**
 * 使用java.util.function.Function作为基类
 * Created by August.Zhou on 2017/6/5 16:51.
 */
@FunctionalInterface
public interface RedisAction<R> extends Function<Jedis, R> {


    /**
     * 这个方法里面只能执行redis相关的操作。
     *
     * @param jedis
     * @return
     */
    @Override
    R apply(Jedis jedis);


}
