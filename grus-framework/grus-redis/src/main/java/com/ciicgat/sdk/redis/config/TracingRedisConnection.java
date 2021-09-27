/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.config;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.redis.RedisSpanDecorator;
import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.AbstractRedisConnection;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisHashCommands;
import org.springframework.data.redis.connection.RedisHyperLogLogCommands;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.connection.RedisPipelineException;
import org.springframework.data.redis.connection.RedisScriptingCommands;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.connection.RedisSetCommands;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.Subscription;
import org.springframework.data.redis.core.types.Expiration;

import java.util.List;
import java.util.function.Supplier;


/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-07-31 10:05
 */
class TracingRedisConnection extends AbstractRedisConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(TracingRedisConnection.class);


    private final RedisConnection redisConnection;
    private final RedisSetting redisSetting;
    private final String instance;

    private final RedisSpanDecorator redisSpanDecorator = RedisSpanDecorator.STANDARD_TAGS;

    TracingRedisConnection(RedisConnection redisConnection, RedisSetting redisSetting) {
        this.redisConnection = redisConnection;
        this.redisSetting = redisSetting;
        this.instance = redisSetting.instanceName();
    }

    private <T> T trace(String commandName, Supplier<T> callback) {
        Span rootSpan = Spans.getRootSpan();
        Tracer tracer = GlobalTracer.get();
        final Span span = tracer.buildSpan("executeRedisCMD")
                .asChildOf(rootSpan == NoopSpan.INSTANCE ? null : rootSpan)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .withTag("command", commandName == null ? "unknown" : commandName)
                .start();
        Tags.DB_INSTANCE.set(span, instance);
        Tags.DB_TYPE.set(span, "redis");
        redisSpanDecorator.onRequest(span);
        try {
            T result = callback.get();
            redisSpanDecorator.onResponse(span);
            return result;
        } catch (Throwable e) {
            Alert.send("redis error:" + redisSetting.toString(), e);
            redisSpanDecorator.onError(e, span);
            LOGGER.error("redis error:" + redisSetting.toString(), e);
            throw e;
        } finally {
            span.finish();
            long duration = SpanUtil.getDurationMilliSeconds(span);
            SlowLogger.logEvent(Module.REDIS, duration, "lettuce redis slow");
        }
    }


    @Override
    public RedisGeoCommands geoCommands() {
        return redisConnection.geoCommands();
    }

    @Override
    public RedisHashCommands hashCommands() {
        return redisConnection.hashCommands();
    }

    @Override
    public RedisHyperLogLogCommands hyperLogLogCommands() {
        return redisConnection.hyperLogLogCommands();
    }

    @Override
    public RedisKeyCommands keyCommands() {
        return redisConnection.keyCommands();
    }

    @Override
    public RedisListCommands listCommands() {
        return redisConnection.listCommands();
    }

    @Override
    public RedisSetCommands setCommands() {
        return redisConnection.setCommands();
    }

    @Override
    public RedisScriptingCommands scriptingCommands() {
        return redisConnection.scriptingCommands();
    }

    @Override
    public RedisServerCommands serverCommands() {
        return redisConnection.serverCommands();
    }

    @Override
    public RedisStreamCommands streamCommands() {
        return redisConnection.streamCommands();
    }

    @Override
    public RedisStringCommands stringCommands() {
        return redisConnection.stringCommands();
    }

    @Override
    public RedisZSetCommands zSetCommands() {
        return redisConnection.zSetCommands();
    }

    @Override
    public void close() throws DataAccessException {
        redisConnection.close();
    }

    @Override
    public boolean isClosed() {
        return redisConnection.isClosed();
    }

    @Override
    public Object getNativeConnection() {
        return redisConnection.getNativeConnection();
    }

    @Override
    public boolean isQueueing() {
        return redisConnection.isQueueing();
    }

    @Override
    public boolean isPipelined() {
        return redisConnection.isPipelined();
    }

    @Override
    public void openPipeline() {
        redisConnection.openPipeline();
    }

    @Override
    public List<Object> closePipeline() throws RedisPipelineException {
        return redisConnection.closePipeline();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return redisConnection.getSentinelConnection();
    }

    @Override
    public Object execute(String command, byte[]... args) {
        return trace("execute", () -> redisConnection.execute(command, args));
    }

    @Override
    public void select(int dbIndex) {
        redisConnection.select(dbIndex);
    }

    @Override
    public byte[] echo(byte[] message) {
        return redisConnection.echo(message);
    }

    @Override
    public String ping() {
        return redisConnection.ping();
    }

    @Override
    public boolean isSubscribed() {
        return redisConnection.isSubscribed();
    }

    @Override
    public Subscription getSubscription() {
        return redisConnection.getSubscription();
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        return redisConnection.publish(channel, message);
    }

    @Override
    public void subscribe(MessageListener listener, byte[]... channels) {
        redisConnection.subscribe(listener, channels);
    }

    @Override
    public void pSubscribe(MessageListener listener, byte[]... patterns) {
        redisConnection.pSubscribe(listener, patterns);
    }

    @Override
    public void multi() {
        redisConnection.multi();
    }

    @Override
    public List<Object> exec() {
        return redisConnection.exec();
    }

    @Override
    public void discard() {
        redisConnection.discard();
    }

    @Override
    public void watch(byte[]... keys) {
        redisConnection.watch(keys);
    }

    @Override
    public void unwatch() {
        redisConnection.unwatch();
    }

    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        return trace("hSet", () -> redisConnection.hSet(key, field, value));
    }

    @Override
    public byte[] hGet(byte[] key, byte[] field) {
        return trace("hGet", () -> redisConnection.hGet(key, field));
    }

    @Override
    public Boolean exists(byte[] key) {
        return trace("exists", () -> redisConnection.exists(key));
    }

    @Override
    public Long exists(byte[]... keys) {
        return trace("exists", () -> redisConnection.exists(keys));
    }

    @Override
    public Long del(byte[]... keys) {
        return trace("del", () -> redisConnection.del(keys));
    }

    @Override
    public Boolean expire(byte[] key, long seconds) {
        return trace("expire", () -> redisConnection.expire(key, seconds));
    }

    @Override
    public Long rPush(byte[] key, byte[]... values) {
        return trace("rPush", () -> redisConnection.rPush(key, values));
    }

    @Override
    public Long lPush(byte[] key, byte[]... values) {
        return trace("lPush", () -> redisConnection.lPush(key, values));
    }

    @Override
    public void shutdown() {
        redisConnection.shutdown();
    }

    @Override
    public void shutdown(ShutdownOption option) {
        redisConnection.shutdown(option);
    }

    @Override
    public String getClientName() {
        return redisConnection.getClientName();
    }

    @Override
    public byte[] get(byte[] key) {
        return trace("get", () -> redisConnection.get(key));
    }

    @Override
    public byte[] getSet(byte[] key, byte[] value) {
        return trace("getSet", () -> redisConnection.getSet(key, value));
    }

    @Override
    public Boolean set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        return trace("set", () -> redisConnection.set(key, value, expiration, option));
    }

    @Override
    public Boolean set(byte[] key, byte[] value) {
        return trace("set", () -> redisConnection.set(key, value));
    }

    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return trace("setNX", () -> redisConnection.setNX(key, value));
    }

    @Override
    public Boolean setEx(byte[] key, long seconds, byte[] value) {
        return trace("setEx", () -> redisConnection.setEx(key, seconds, value));
    }

    @Override
    public Long incr(byte[] key) {
        return trace("incr", () -> redisConnection.incr(key));
    }
}
