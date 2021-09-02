/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.concurrent.TimeUnit;


/**
 * 二级缓存实现，第一级为本地内存，第二级为redis
 * <p>
 * Created by August.Zhou on 2020/12/15 15:46.
 */
public class LocalCache extends AbstractCache<CacheConfig.Local> implements ILocalCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCache.class);
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> localCache;
    protected final RedisSerializer<Object> valueSerializer;

    public LocalCache(String name, RedisCacheManager redisCacheManager, CacheConfig.Local local) {
        super(name, redisCacheManager, local);
        this.valueSerializer = redisCacheManager.getRedisCacheConfig().getSerializer();

        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(local.getMaximumSize())
                .initialCapacity(local.getInitialCapacity());
        if (local.getExpireSeconds() > 0) {
            caffeine.expireAfterWrite(local.getExpireSeconds(), TimeUnit.SECONDS);
        }
        this.localCache = caffeine.build();
        this.redisCacheManager.initMessageListener();
    }


    @Override
    public Object getNativeCache() {
        return localCache;
    }

    @Override
    protected ValueWrapper get0(Object key) {
        Object value = localCache.getIfPresent(key);
        if (value == null) {
            return null;
        }
        return new SimpleValueWrapper(value == NULL ? null : this.config.isSerialize() ? valueSerializer.deserialize((byte[]) value) : value);
    }

    @Override
    protected void put0(Object key, Object value) {
        localCache.put(key, value == null ? NULL : this.config.isSerialize() ? valueSerializer.serialize(value) : value);
    }

    @Override
    protected void evict0(Object key) {
        localCache.invalidate(key);
        redisCacheManager.sendEvictMessage(key, this.name);
    }


    @Override
    public void clear() {
        localCache.invalidateAll();
        redisCacheManager.sendEvictMessage(null, this.name);
    }

    /**
     * 清理本地缓存
     *
     * @param key 缓存key
     */
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
