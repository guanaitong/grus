/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.redis.config.RedisSetting;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;


/**
 * Created by August.Zhou on 2019-04-25 17:43.
 */
class RedisExecutorImpl implements RedisExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExecutorImpl.class);
    private final RedisSetting redisSetting;
    private final Pool<Jedis> pool;

    private final String instance;

    RedisExecutorImpl(RedisSetting redisSetting, Pool<Jedis> pool) {
        this.redisSetting = redisSetting;
        this.pool = pool;
        this.instance = redisSetting.instanceName();
    }

    @Override
    public final <T> T execute(RedisAction<T> redisAction) {
        Tracer tracer = OpenTelemetrys.get();
        Span span = tracer.spanBuilder("executeRedisCMD").setSpanKind(SpanKind.CLIENT).setParent(Context.current()).startSpan();
        Jedis jedis = pool.getResource();
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("component", "redis");
            span.setAttribute("db.type", "redis");
            span.setAttribute("db.instance", instance);
            return redisAction.apply(jedis);
        } catch (Throwable e) {
            Alert.send("redis error:" + redisSetting.toString(), e);
            LOGGER.error("redis error:" + redisSetting, e);
            throw e;
        } finally {
            CloseUtils.close(jedis);
            span.end();
            SlowLogger.logEvent(Module.REDIS, span, redisAction.toString());
        }
    }


    @Override
    public void close() throws Exception {
        CloseUtils.close(pool);
    }
}
