/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolAbstract;

/**
 * Created by August.Zhou on 2019-04-25 17:43.
 */
class RedisExecutorImpl implements RedisExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExecutorImpl.class);


    private final RedisSetting redisSetting;

    private final JedisPoolAbstract pool;

    RedisExecutorImpl(RedisSetting redisSetting, JedisPoolAbstract pool) {
        this.redisSetting = redisSetting;
        this.pool = pool;
    }

    @Override
    public final <T> T execute(RedisAction<T> redisAction) {
        long start = System.currentTimeMillis();
        Jedis jedis = pool.getResource();
        try {
            T result = redisAction.apply(jedis);
            return result;
        } catch (Throwable e) {
            FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.REDIS, "redis error:" + redisSetting.toString(), e);
            LOGGER.error("redis error:" + redisSetting.toString(), e);
            throw e;
        } finally {
            CloseUtils.close(jedis);
            long duration = System.currentTimeMillis() - start;
            SlowLogger.logEvent(Module.REDIS, duration, redisAction.toString());
        }
    }


    @Override
    public void close() throws Exception {
        CloseUtils.close(pool);
    }
}
