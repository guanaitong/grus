/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.refresh;

import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.springcache.AbstractCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 根据频率刷新缓存
 *
 * @Author: August
 * @Date: 2021/4/13 15:23
 */
public class FrequencyCacheRefresher extends AbstractCacheRefresher {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrequencyCacheRefresher.class);
    private final com.github.benmanes.caffeine.cache.Cache<String, Boolean> localCache;
    private final Expiration expiration;

    public FrequencyCacheRefresher() {
        this(300);
    }

    public FrequencyCacheRefresher(int frequencySeconds) {
        super();
        this.localCache = newLocalCache(frequencySeconds);
        this.expiration = Expiration.from(frequencySeconds, TimeUnit.SECONDS);
    }

    public FrequencyCacheRefresher(Executor executor, int frequencySeconds) {
        super(executor);
        this.localCache = newLocalCache(frequencySeconds);
        this.expiration = Expiration.from(frequencySeconds, TimeUnit.SECONDS);
    }

    private com.github.benmanes.caffeine.cache.Cache<String, Boolean> newLocalCache(int frequencySeconds) {
        return Caffeine.newBuilder()
                .expireAfterWrite(Math.max(60, frequencySeconds / 2), TimeUnit.SECONDS)
                .initialCapacity(1024)
                .maximumSize(102400L)
                .build();
    }


    /**
     * 每隔N秒，异步刷新下缓存
     *
     * @param cache
     * @param key，需要符合拼接规则
     * @param valueLoader
     */
    @Override
    public void mayRefresh(AbstractCache cache, String key, Callable<Object> valueLoader) {
        final String cacheKey = makeGlobalCacheKey(cache, key);
        // 1、先过本地缓存、再通过redis，两层频率限制。
        // 2、本地缓存可以降低redis的访问量
        // 3、redis可以控制应用所有节点的刷新频率.
        // 4、所以本地缓存的过期事件自动设置为redis过期事件的二分之一
        localCache.get(cache.getName() + ":::" + key, o -> {
            try (RedisConnection connection = cache.getRedisConnectionFactory().getConnection()) {
                Boolean set = connection.set(Bytes.toBytes(cacheKey), ArrayUtils.EMPTY_BYTE_ARRAY, expiration, RedisStringCommands.SetOption.ifAbsent());
                if (set != null && set.booleanValue()) {
                    refresh(cache, key, valueLoader);
                }
            } catch (Exception e) {  //如果刷新失败，那么就错过这次机会了。下个循环开始
                LOGGER.error(cacheKey, e);
            }
            return Boolean.TRUE;
        });
    }

    @Override
    public void recordCacheInit(AbstractCache cache, String key) {
        final String cacheKey = makeGlobalCacheKey(cache, key);
        localCache.put(cacheKey, Boolean.TRUE);
        try (RedisConnection connection = cache.getRedisConnectionFactory().getConnection()) {
            connection.set(Bytes.toBytes(cacheKey), ArrayUtils.EMPTY_BYTE_ARRAY, expiration, RedisStringCommands.SetOption.upsert());
        } catch (Exception e) {
            LOGGER.error(cacheKey, e);
        }
    }

    private static String makeGlobalCacheKey(AbstractCache cache, String key) {
        return cache.getName() + ":::" + key;
    }
}
