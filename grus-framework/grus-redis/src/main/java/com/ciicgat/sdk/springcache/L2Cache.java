/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
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
            caffeine.scheduler(Scheduler.systemScheduler());
        }

        this.localCache = caffeine.build();
        redisCacheManager.initMessageListener();
    }

    @Override
    public Object getNativeCache() {
        return localCache;
    }

    @Override
    protected BytesValue getValue(Object key) {
        Object value = localCache.getIfPresent(key);
        if (value == null) {
            // 从redis中取
            BytesValue redisBytesValue = super.getValue(key);
            if (redisBytesValue == null) {
                return null;
            }
            if (NULL_BYTES_VALUE == redisBytesValue && !cacheNull) {
                return NULL_BYTES_VALUE;
            }
            localCache.put(key, this.config.isSerialize() ? redisBytesValue.getBytes() : redisBytesValue.getValue());
            return redisBytesValue;
        } else if (value == NULL) {
            return NULL_BYTES_VALUE;
        }
        if (this.config.isSerialize()) {
            byte[] bytesValue = (byte[]) value;
            return new BytesValue(bytesValue, valueSerializer.deserialize(bytesValue));
        } else {
            return new BytesValue(null, value);
        }
    }


    @Override
    protected void put0(Object key, Object value) {
        if (Objects.isNull(value)) {
            localCache.put(key, NULL);
            saveRedisCache(key, NULL_BYTES_VALUE);
            return;
        }
        if (this.config.isSerialize()) {
            BytesValue bytesValue = new BytesValue(valueSerializer.serialize(value), value);
            localCache.put(key, bytesValue.getBytes());
            saveRedisCache(key, bytesValue);
        } else {
            localCache.put(key, value);
            saveRedisCache(key, new BytesValue(valueSerializer.serialize(value), value));
        }
    }

    @Override
    public void putNewValue(Object key, Object value) {
        if (putIgnoreException(key, value)) {
            sendEvictMessage(key);
        }
    }

    @Override
    public void evict0(Object key) {
        super.evict0(key);
        localCache.invalidate(key);
        sendEvictMessage(key);
    }


    @Override
    public void clear() {
        localCache.invalidateAll();
        sendEvictMessage(null);
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
