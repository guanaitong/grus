/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.lock;

import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.redis.RedisExecutor;
import com.ciicgat.sdk.redis.config.RedisSetting;

/**
 * Created by August.Zhou on 2019-08-01 17:51.
 */
public class HARedis {

    private static final RedisExecutor redisExecutor;

    static {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("for-test-java");
        RedisSetting redisSetting = configCollection.getBean("redis-config.json", RedisSetting.class);
        redisExecutor = redisSetting.newRedisExecutor();
    }

    public static RedisExecutor getRedisExecutor() {
        return redisExecutor;
    }

}
