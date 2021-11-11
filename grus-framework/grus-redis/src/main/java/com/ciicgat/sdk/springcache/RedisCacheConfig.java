/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.redis.config.RedisSetting;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Created by August.Zhou on 2019-08-21 11:35.
 */
public class RedisCacheConfig {

    private RedisSerializer serializer = RedisSerializer.java();

    private String prefix;

    private CacheConfigFunc cacheConfigFunc = CacheConfigFunc.DEFAULT;

    private RedisKeyListener redisKeyListener = RedisKeyListener.DEFAULT;

    private CacheRefresher cacheRefresher = CacheRefresher.NOOP;

    /**
     * 使用gzip压缩存储缓存数据，缓存数据大时建议开启
     */
    private boolean useGzip;
    /**
     * 是否cacheNull值
     */
    private boolean cacheNull;

    /**
     * enableLocalCache=true时，用于订阅删除key的通道名，如果不填写，默认使用应用名
     */
    private String channel;

    private RedisSetting redisSetting;


    public RedisCacheConfig() {
    }

    public RedisSerializer getSerializer() {
        return serializer;
    }

    public RedisCacheConfig setSerializer(RedisSerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public CacheRefresher getCacheRefresher() {
        return this.cacheRefresher;
    }

    public RedisCacheConfig setCacheRefresher(final CacheRefresher cacheRefresher) {
        this.cacheRefresher = cacheRefresher;
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

    public RedisKeyListener getRedisKeyListener() {
        return redisKeyListener;
    }

    public RedisCacheConfig setRedisKeyListener(RedisKeyListener redisKeyListener) {
        this.redisKeyListener = redisKeyListener;
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

    public CacheConfig getCacheConfig(String cacheName) {
        return this.cacheConfigFunc.getCacheConfig(cacheName);
    }
}
