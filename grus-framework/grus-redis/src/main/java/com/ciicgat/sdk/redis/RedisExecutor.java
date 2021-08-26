/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

/**
 * Created by August.Zhou on 2017/6/5 16:30.
 */
public interface RedisExecutor extends AutoCloseable {


    <T> T execute(RedisAction<T> redisAction);

}
