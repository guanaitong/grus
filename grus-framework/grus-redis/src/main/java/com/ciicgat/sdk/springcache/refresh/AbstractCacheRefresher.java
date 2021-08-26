/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.refresh;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.springcache.AbstractCache;
import com.ciicgat.sdk.springcache.CacheRefresher;
import com.ciicgat.sdk.trace.TraceThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: August
 * @Date: 2021/7/12 11:33
 */
public abstract class AbstractCacheRefresher implements CacheRefresher {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrequencyCacheRefresher.class);

    private final Executor executor;

    public AbstractCacheRefresher() {
        this(new TraceThreadPoolExecutor(4,
                4,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(128),
                Threads.newDaemonThreadFactory("cacheRefresher", Thread.MIN_PRIORITY),
                Threads.LOGGER_REJECTEDEXECUTIONHANDLER
        ));
    }

    public AbstractCacheRefresher(final Executor executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    void refresh(AbstractCache cache, String key, Callable<Object> valueLoader) {
        executor.execute(() -> {
            try {
                LOGGER.info(String.format("start refresh,cache %s,key %s", cache.getName(), key));
                cache.put(key, valueLoader.call());
                LOGGER.info(String.format("refresh success,cache %s,key %s", cache.getName(), key));
            } catch (Exception e) {
                LOGGER.warn(String.format("refresh failed,cache %s,key %s", cache.getName(), key), e);
            }
        });
    }
}
