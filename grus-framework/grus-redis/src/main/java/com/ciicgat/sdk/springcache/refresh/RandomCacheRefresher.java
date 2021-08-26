/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.refresh;

import com.ciicgat.sdk.springcache.AbstractCache;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: August
 * @Date: 2021/7/12 11:24
 */
public class RandomCacheRefresher extends AbstractCacheRefresher {
    private final double sample;

    public RandomCacheRefresher(double sample) {
        super();
        this.sample = sample;
    }

    public RandomCacheRefresher(Executor executor, double sample) {
        super(executor);
        this.sample = sample;
    }

    @Override
    public void mayRefresh(AbstractCache cache, String key, Callable<Object> valueLoader) {
        if (ThreadLocalRandom.current().nextDouble(1) <= sample) {
            refresh(cache, key, valueLoader);
        }
    }

    @Override
    public void recordCacheInit(AbstractCache cache, String key) {

    }
}
