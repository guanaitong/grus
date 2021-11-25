/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.refresh;

import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.springcache.AbstractCache;
import com.ciicgat.sdk.util.system.Systems;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;

import java.util.concurrent.TimeUnit;

/**
 * @author August
 * @date 2021/11/25 9:47 PM
 */
class FrequencyRefreshPolicy extends RefreshPolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrequencyRefreshPolicy.class);
    private final com.github.benmanes.caffeine.cache.Cache<String, Boolean> localCache;
    private final Expiration expiration;

    FrequencyRefreshPolicy(int frequencySeconds) {
        this.localCache = newLocalCache(frequencySeconds);
        this.expiration = Expiration.from(frequencySeconds, TimeUnit.SECONDS);
    }

    private com.github.benmanes.caffeine.cache.Cache<String, Boolean> newLocalCache(int frequencySeconds) {
        return Caffeine.newBuilder()
                .expireAfterWrite(Math.max(1, frequencySeconds / 2), TimeUnit.SECONDS)
                .scheduler(Scheduler.systemScheduler())
                .initialCapacity(1024)
                .maximumSize(102400L)
                .build();
    }

    @Override
    public boolean mayRefresh(boolean global, AbstractCache cache, String key) {
        final String localCacheKey = makeLocalCacheKey(global, cache, key);
        // 1、先过本地缓存、再通过redis，两层频率限制。
        // 2、本地缓存可以降低redis的访问量
        // 3、redis可以控制应用所有节点的刷新频率.
        // 4、所以本地缓存的过期事件自动设置为redis过期事件的二分之一
        if (localCache.getIfPresent(localCacheKey) == null) {
            try (RedisConnection connection = cache.getRedisConnectionFactory().getConnection()) {
                Boolean set = connection.set(makeRedisCacheKey(cache, key), ArrayUtils.EMPTY_BYTE_ARRAY, expiration, RedisStringCommands.SetOption.ifAbsent());
                if (set != null && set.booleanValue()) {
                    return true;
                }
            } catch (Exception e) {  //如果刷新失败，那么就错过这次机会了。下个循环开始
                LOGGER.error(localCacheKey, e);
            } finally {
                localCache.put(localCacheKey, Boolean.TRUE);
            }
        }

        return false;
    }

    @Override
    public void recordCacheInit(boolean global, AbstractCache cache, String key) {
        final String cacheKey = makeLocalCacheKey(global, cache, key);
        localCache.put(cacheKey, Boolean.TRUE);
        try (RedisConnection connection = cache.getRedisConnectionFactory().getConnection()) {
            connection.set(makeRedisCacheKey(cache, key), ArrayUtils.EMPTY_BYTE_ARRAY, expiration, RedisStringCommands.SetOption.upsert());
        } catch (Exception e) {
            LOGGER.error(cacheKey, e);
        }
    }

    private static String makeLocalCacheKey(boolean global, AbstractCache cache, String key) {
        return global ? cache.getName() + ":::" + key : key;
    }

    private static byte[] makeRedisCacheKey(AbstractCache cache, String key) {
        return Bytes.toBytes(Systems.APP_NAME + "_EX_" + cache.getName() + ":::" + key);
    }
}
