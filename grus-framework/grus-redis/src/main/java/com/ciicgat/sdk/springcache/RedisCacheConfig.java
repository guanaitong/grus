/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.springcache.event.CacheChangeListener;
import com.ciicgat.sdk.springcache.refresh.RefreshPolicy;
import com.ciicgat.sdk.trace.TraceThreadPoolExecutor;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by August.Zhou on 2019-08-21 11:35.
 */
public class RedisCacheConfig {

    private RedisSerializer<Object> serializer = RedisSerializer.java();

    private String prefix;

    private CacheConfigFunc cacheConfigFunc = CacheConfigFunc.DEFAULT;

    private CacheChangeListener cacheChangeListener = CacheChangeListener.DEFAULT;


    /**
     * 使用gzip压缩存储缓存数据，缓存数据大时建议开启
     */
    private boolean useGzip;
    /**
     * 是否cacheNull值
     */
    private boolean cacheNull;

    /**
     * 全局刷新策略
     */
    private RefreshPolicy refreshPolicy = RefreshPolicy.never();

    private Supplier<Executor> refreshExecutor = () -> new TraceThreadPoolExecutor(2,
            2,
            0,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(128),
            Threads.newDaemonThreadFactory("cacheRefresher", Thread.MIN_PRIORITY),
            Threads.LOGGER_REJECTEDEXECUTIONHANDLER);

    /**
     * enableLocalCache=true时，用于订阅删除key的通道名，如果不填写，默认使用应用名
     */
    private String channel;

    private RedisSetting redisSetting;


    public RedisCacheConfig() {
    }

    public RedisSerializer<Object> getSerializer() {
        return serializer;
    }

    public RedisCacheConfig setSerializer(RedisSerializer<Object> serializer) {
        this.serializer = serializer;
        return this;
    }


    public String getPrefix() {
        return prefix;
    }

    public RedisCacheConfig setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CacheConfigFunc getCacheConfigFunc() {
        return cacheConfigFunc;
    }

    public RedisCacheConfig setCacheConfigFunc(CacheConfigFunc cacheConfigFunc) {
        this.cacheConfigFunc = cacheConfigFunc;
        return this;
    }

    public CacheChangeListener getRedisKeyListener() {
        return cacheChangeListener;
    }

    public RedisCacheConfig setCacheChangeListener(CacheChangeListener cacheChangeListener) {
        this.cacheChangeListener = cacheChangeListener;
        return this;
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    public RedisCacheConfig setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
        return this;
    }

    public RedisSetting getRedisSetting() {
        return redisSetting;
    }

    public RedisCacheConfig setRedisSetting(RedisSetting redisSetting) {
        this.redisSetting = redisSetting;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public RedisCacheConfig setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public boolean isCacheNull() {
        return cacheNull;
    }

    public RedisCacheConfig setCacheNull(boolean cacheNull) {
        this.cacheNull = cacheNull;
        return this;
    }

    public RefreshPolicy getRefreshPolicy() {
        return refreshPolicy;
    }

    public RedisCacheConfig setRefreshPolicy(RefreshPolicy refreshPolicy) {
        this.refreshPolicy = Objects.requireNonNull(refreshPolicy);
        return this;
    }

    public Supplier<Executor> getRefreshExecutor() {
        return refreshExecutor;
    }

    public RedisCacheConfig setRefreshExecutor(Supplier<Executor> refreshExecutor) {
        this.refreshExecutor = refreshExecutor;
        return this;
    }

    public CacheConfig getCacheConfig(String cacheName) {
        return this.cacheConfigFunc.getCacheConfig(cacheName);
    }
}
