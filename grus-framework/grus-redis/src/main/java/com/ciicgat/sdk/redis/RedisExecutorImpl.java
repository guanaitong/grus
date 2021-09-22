/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.metrics.Event;
import com.ciicgat.grus.metrics.ModuleEventType;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.redis.config.RedisSetting;
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

    private final String instance;

    RedisExecutorImpl(RedisSetting redisSetting, JedisPoolAbstract pool) {
        this.redisSetting = redisSetting;
        this.pool = pool;
        this.instance = redisSetting.instanceName();
    }

    @Override
    public final <T> T execute(RedisAction<T> redisAction) {
        String[] tags = new String[]{"db.instance", instance, "command", "execute"};
        Event event = ModuleEventType.REDIS_REQUEST.newEvent("execute", tags, redisAction.toString());

        Jedis jedis = pool.getResource();
        try {
            T result = redisAction.apply(jedis);
            return result;
        } catch (Throwable e) {
            LOGGER.error("redis error:" + redisSetting.toString(), e);
            event.error("redis error:" + redisSetting, e);
            throw e;
        } finally {
            CloseUtils.close(jedis);
            CloseUtils.close(event);
        }
    }


    @Override
    public void close() throws Exception {
        CloseUtils.close(pool);
    }
}
