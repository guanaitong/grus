/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.concurrent.Callable;

/**
 * @Author: August
 * @Date: 2021/4/15 9:46
 */
public abstract class AbstractCache<C extends CacheConfig> implements Cache {
    protected static final Object NULL = new Object();
    protected final String name;
    protected final RedisCacheManager redisCacheManager;
    protected final C config;
    protected final CacheRefresher cacheRefresher;
    protected final RedisConnectionFactory redisConnectionFactory;

    public AbstractCache(String name, RedisCacheManager redisCacheManager, C config) {
        this.name = name;
        this.redisCacheManager = redisCacheManager;
        this.config = config;
        this.cacheRefresher = this.redisCacheManager.getCacheRefresher();
        this.redisConnectionFactory = this.redisCacheManager.getRedisConnectionFactory();
    }

    @Override
    public final <T> T get(final Object key, final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String getName() {
        return name;
    }

    public final RedisConnectionFactory getRedisConnectionFactory() {
        return this.redisConnectionFactory;
    }

    @Override
    public final <T> T get(final Object key, final Callable<T> valueLoader) {
        String actualKey;
        if (key instanceof CacheKey) {
            actualKey = ((CacheKey) key).cacheKey();
        } else {
            actualKey = key.toString();
        }
        final Cache.ValueWrapper valueWrapper = get(actualKey);
        if (valueWrapper == null) {
            final Object o;
            try {
                o = valueLoader.call();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex.getCause());
            }

            put(actualKey, o);
            this.cacheRefresher.recordCacheInit(this, actualKey);
            return (T) o;
        } else {
            this.cacheRefresher.mayRefresh(this, actualKey, (Callable<Object>) valueLoader);
            return (T) valueWrapper.get();
        }
    }
}
