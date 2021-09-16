/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.refresh;

import com.ciicgat.sdk.springcache.AbstractCache;
import com.ciicgat.sdk.springcache.IRedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * 根据过期时间刷新缓存
 *
 * @Author: August
 * @Date: 2021/4/13 15:28
 */
public class NearExpiredCacheRefresher extends AbstractCacheRefresher {
    private static final Logger LOGGER = LoggerFactory.getLogger(NearExpiredCacheRefresher.class);
    private final int preLoadSeconds;


    public NearExpiredCacheRefresher(int preLoadSeconds) {
        super();
        this.preLoadSeconds = checkPreLoadSeconds(preLoadSeconds);
    }

    public NearExpiredCacheRefresher(Executor executor, int preLoadSeconds) {
        super(executor);
        this.preLoadSeconds = checkPreLoadSeconds(preLoadSeconds);
    }

    private static int checkPreLoadSeconds(int preLoadSeconds) {
        if (preLoadSeconds <= 0) {
            throw new IllegalArgumentException("preLoadSeconds must positive");
        }
        return preLoadSeconds;
    }

    /**
     * 每隔N秒，异步刷新下缓存
     *
     * @param cache
     * @param key，需要符合拼接规则
     * @param valueLoader
     */
    @Override
    public void mayRefresh(AbstractCache cache, String key, Cache.ValueWrapper oldValueWrapper, Callable<Object> valueLoader) {
        try {
            if (cache instanceof IRedisCache) {
                long ttl = ((IRedisCache) cache).ttl(key);
                if (ttl > 0 && ttl <= preLoadSeconds) {
                    compareThenRefresh(cache, key, oldValueWrapper, valueLoader);
                }
            }
        } catch (Exception e) {
            LOGGER.error(cache.getName() + key, e);
        }
    }

    @Override
    public void recordCacheInit(AbstractCache cache, String key) {

    }
}
