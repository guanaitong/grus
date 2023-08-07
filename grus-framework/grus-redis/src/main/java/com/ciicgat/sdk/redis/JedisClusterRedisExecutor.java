/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.redis.config.RedisSetting;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

/**
 * Created by August.Zhou on 2023/5/5 13:40
 */
class JedisClusterRedisExecutor implements RedisExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisClusterRedisExecutor.class);

    private final RedisSetting redisSetting;
    private final JedisCluster jedisCluster;

    JedisClusterRedisExecutor(RedisSetting redisSetting, JedisCluster jedisCluster) {
        this.redisSetting = redisSetting;
        this.jedisCluster = jedisCluster;
    }

    @Override
    public <T> T execute(RedisAction<T> redisAction) {
        Tracer tracer = OpenTelemetrys.getTracer();
        Span span = tracer.spanBuilder("executeRedisCMD").setSpanKind(SpanKind.CLIENT).setParent(Context.current()).startSpan();
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("component", "redis");
            span.setAttribute("db.type", "redis");
            return redisAction.apply(jedisCluster);
        } catch (Throwable e) {
            Alert.send("redis error:" + redisSetting.toString(), e);
            LOGGER.error("redis error:" + redisSetting, e);
            throw e;
        } finally {
            span.end();
            SlowLogger.logEvent(Module.REDIS, span, redisAction.toString());
        }
    }

    @Override
    public void close() throws Exception {
        CloseUtils.close(jedisCluster);
    }
}
