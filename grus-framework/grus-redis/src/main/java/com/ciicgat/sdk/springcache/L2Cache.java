/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * 二级缓存实现，第一级为本地内存，第二级为redis
 * <p>
 * Created by August.Zhou on 2020/12/15 15:46.
 */
public class L2Cache extends RedisCache<CacheConfig.LocalRedis> implements ILocalCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(L2Cache.class);
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> localCache;

    public L2Cache(String name, RedisCacheManager redisCacheManager, CacheConfig.LocalRedis localRedis) {
        super(name, redisCacheManager, localRedis);

        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(this.config.getMaximumSize())
                .initialCapacity(this.config.getInitialCapacity());

        if (localRedis.getLocalExpireSeconds() > 0) {
            caffeine.expireAfterWrite(localRedis.getLocalExpireSeconds(), TimeUnit.SECONDS);
        }

        this.localCache = caffeine.build();
        this.redisCacheManager.initMessageListener();
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    protected Object getValue(Object key) {
        Object localValue = localCache.getIfPresent(key);
        if (localValue == null) {
            // 从redis中取
            Object redisValue = super.getValue(key);
            if (redisValue != null) {
                saveLocalCache(key, redisValue);
            }
            return redisValue;
        } else if (localValue == NULL) {
            return localValue;
        }
        if (this.config.isSerialize()) {
            byte[] bytesValue = (byte[]) localValue;
            try {
                return valueSerializer.deserialize(bytesValue);
            } catch (Exception e) {
                LOGGER.warn("deserialize error,name= " + name + ",key=" + key, e);
                return null;
            }
        } else {
            return localValue;
        }
    }

    private void saveLocalCache(Object key, Object value) {
        if (value == null || value == NULL) {
            return;
        }
        if (this.config.isSerialize()) {
            try {
                localCache.put(key, valueSerializer.serialize(value));
            } catch (Exception e) {
                LOGGER.warn("serialize error,name= " + name + ",key=" + key, e);
            }
        } else {
            localCache.put(key, value);
        }
    }

    @Override
    public void put(Object key, Object value) {
        super.put(key, value);
        saveLocalCache(key, value);
    }

    @Override
    public void evict(Object key) {
        super.evict(key);
        localCache.invalidate(key);
        redisCacheManager.sendEvictMessage(key, this.name);
    }


    @Override
    public void clear() {
        localCache.invalidateAll();
        redisCacheManager.sendEvictMessage(null, this.name);
    }

    @Override
    public void clearLocal(Object key) {
        LOGGER.info("clear local cache, the key is : {}", key);
        if (key == null) {
            localCache.invalidateAll();
        } else {
            localCache.invalidate(key);
        }
    }
}
