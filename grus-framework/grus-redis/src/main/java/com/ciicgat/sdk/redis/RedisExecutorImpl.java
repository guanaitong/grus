/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
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
    private final RedisSpanDecorator redisSpanDecorator = RedisSpanDecorator.STANDARD_TAGS;

    private final String instance;

    RedisExecutorImpl(RedisSetting redisSetting, JedisPoolAbstract pool) {
        this.redisSetting = redisSetting;
        this.pool = pool;
        this.instance = redisSetting.instanceName();
    }

    @Override
    public final <T> T execute(RedisAction<T> redisAction) {
        Span rootSpan = Spans.getRootSpan();
        Tracer tracer = GlobalTracer.get();
        final Span span = tracer.buildSpan("executeRedisCMD")
                .asChildOf(rootSpan == NoopSpan.INSTANCE ? null : rootSpan)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();

        Tags.DB_INSTANCE.set(span, instance);
        Tags.DB_TYPE.set(span, "redis");
        redisSpanDecorator.onRequest(span);

        Jedis jedis = pool.getResource();
        try {
            T result = redisAction.apply(jedis);
            redisSpanDecorator.onResponse(span);
            return result;
        } catch (Throwable e) {
            Alert.send("redis error:" + redisSetting.toString(), e);
            redisSpanDecorator.onError(e, span);
            LOGGER.error("redis error:" + redisSetting, e);
            throw e;
        } finally {
            CloseUtils.close(jedis);
            span.finish();

            long duration = SpanUtil.getDurationMilliSeconds(span);
            SlowLogger.logEvent(Module.REDIS, duration, redisAction.toString());
        }
    }


    @Override
    public void close() throws Exception {
        CloseUtils.close(pool);
    }
}
