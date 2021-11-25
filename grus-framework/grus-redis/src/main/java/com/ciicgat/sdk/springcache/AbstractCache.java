/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.springcache.event.CacheChangeEvent;
import com.ciicgat.sdk.springcache.event.CacheChangeListener;
import com.ciicgat.sdk.springcache.event.CacheChangeType;
import com.ciicgat.sdk.springcache.refresh.RefreshPolicy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.stats.ConcurrentStatsCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @Author: August
 * @Date: 2021/4/15 9:46
 */
public abstract class AbstractCache<C extends CacheConfig> implements Cache {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);
    protected static final Object NULL = new Object();
    protected static final BytesValue NULL_BYTES_VALUE = new BytesValue(Bytes.EMPTY_BYTE_ARRAY, NULL);
    protected final String name;
    private final RedisCacheManager redisCacheManager;
    protected final C config;

    private final RedisConnectionFactory redisConnectionFactory;
    // use caffeine stats counter
    private final ConcurrentStatsCounter statsCounter = new ConcurrentStatsCounter();
    protected final boolean cacheNull;
    protected final RedisSerializer<Object> valueSerializer;
    private boolean bind = false;

    private final RefreshPolicy refreshPolicy;
    private final boolean isGlobalRefreshPolicy;
    private final CacheChangeListener cacheChangeListener;

    public AbstractCache(String name, RedisCacheManager redisCacheManager, C config) {
        this.name = name;
        this.redisCacheManager = redisCacheManager;
        this.config = config;
        this.redisConnectionFactory = this.redisCacheManager.getRedisConnectionFactory();
        final RedisCacheConfig redisCacheConfig = redisCacheManager.getRedisCacheConfig();
        this.cacheChangeListener = redisCacheConfig.getRedisKeyListener();
        RedisSerializer<Object> redisSerializer = Objects.isNull(config.getSerializer()) ? redisCacheConfig.getSerializer() : config.getSerializer();
        if (redisSerializer instanceof GzipRedisSerializer) {
            this.valueSerializer = redisSerializer;
        } else {
            boolean useGzip = Objects.isNull(config.getUseGzip()) ? redisCacheConfig.isUseGzip() : config.getUseGzip().booleanValue();
            this.valueSerializer = useGzip ? new GzipRedisSerializer(redisSerializer) : redisSerializer;
        }
        this.cacheNull = Objects.isNull(config.getCacheNull()) ? redisCacheConfig.isCacheNull() : config.getCacheNull().booleanValue();
        this.isGlobalRefreshPolicy = Objects.isNull(config.getRefreshPolicy());
        this.refreshPolicy = isGlobalRefreshPolicy ? redisCacheConfig.getRefreshPolicy() : config.getRefreshPolicy();
        Objects.requireNonNull(this.refreshPolicy);
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public final CacheStats stats() {
        return statsCounter.snapshot();
    }

    protected final <T> T execute(Function<RedisConnection, T> callback) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            return callback.apply(connection);
        }
    }

    protected final void sendEvictMessage(Object key) {
        redisCacheManager.sendEvictMessage(key, name);
    }


    @Override
    public final ValueWrapper get(Object key) {
        long start = System.nanoTime();
        try {
            ValueWrapper valueWrapper = get0(key);
            statsCounter.recordLoadSuccess(System.nanoTime() - start);
            if (valueWrapper == null) {
                statsCounter.recordMisses(1);
            } else {
                statsCounter.recordHits(1);
            }
            return valueWrapper;
        } catch (Exception e) {
            statsCounter.recordLoadFailure(System.nanoTime() - start);
            LOGGER.warn("get value failed,key=" + key, e);
        }
        return null;
    }

    protected abstract ValueWrapper get0(Object key);

    @Override
    public final void put(Object key, Object value) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    putIgnoreException(key, value);
                }
            });
        } else {
            putIgnoreException(key, value);
        }
    }

    public abstract void putNewValue(Object key, Object value);

    protected boolean putIgnoreException(Object key, Object value) {
        try {
            if (Objects.isNull(value) && !cacheNull) {
                return false;
            }
            put0(key, value);
            cacheChangeListener.onChanged(new CacheChangeEvent(CacheChangeType.PUT, key, this));
            return true;
        } catch (Exception e) {
            LOGGER.warn("save value failed,key=" + key, e);
        }
        return false;
    }

    protected abstract void put0(Object key, Object value);

    @Override
    public final void evict(Object key) {
        evictIgnoreException(key);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    evictIgnoreException(key);
                }
            });
        }
    }

    private void evictIgnoreException(Object key) {
        try {
            statsCounter.recordEviction();
            evict0(key);
            cacheChangeListener.onChanged(new CacheChangeEvent(CacheChangeType.EVICT, key, this));
        } catch (Exception e) {
            LOGGER.warn("evict value failed,key=" + key, e);
        }
    }

    protected abstract void evict0(Object key);

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
            this.refreshPolicy.recordCacheInit(isGlobalRefreshPolicy, this, actualKey);
            return (T) o;
        } else {
            boolean mayRefresh = refreshPolicy.mayRefresh(isGlobalRefreshPolicy, this, actualKey);
            if (mayRefresh) {
                compareThenRefresh(actualKey, valueWrapper, (Callable<Object>) valueLoader);
            }
            return (T) valueWrapper.get();
        }
    }


    public void compareThenRefresh(String key, Cache.ValueWrapper oldValueWrapper, Callable<Object> valueLoader) {
        redisCacheManager.getRefreshExecutor().execute(() -> {
            try {
                LOGGER.info("start refresh,cache {},key {}", this.name, key);
                Object newValue = valueLoader.call();
                Object oldValue = oldValueWrapper.get();
                if (refreshPolicy.isUseEqualFunction() ? Objects.equals(newValue, oldValue) : JSON.equals(newValue, oldValue)) {
                    LOGGER.info("no changed,cache {},key {}", this.name, key);
                    return;
                }
                putNewValue(key, newValue);
                LOGGER.info("refresh success,cache {},key {}", this.name, key);
            } catch (Exception e) {
                LOGGER.warn(String.format("refresh failed,cache %s,key %s", this.name, key), e);
            }
        });
    }


}
