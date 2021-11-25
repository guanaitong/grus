/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.refresh;

import com.ciicgat.sdk.springcache.AbstractCache;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author August
 * @date 2021/11/25 9:46 PM
 */
class RandomRefreshPolicy extends RefreshPolicy {
    private final double sample;

    RandomRefreshPolicy(double sample) {
        this.sample = sample;
        if (sample < 0 || sample > 1) {
            throw new IllegalArgumentException("sample should between 0-1");
        }
    }

    @Override
    public boolean mayRefresh(boolean global, AbstractCache cache, String key) {
        return ThreadLocalRandom.current().nextDouble(1) <= sample;
    }

    @Override
    public void recordCacheInit(boolean global, AbstractCache cache, String key) {
        ThreadLocalRandom.current().nextDouble(1);
    }
}
