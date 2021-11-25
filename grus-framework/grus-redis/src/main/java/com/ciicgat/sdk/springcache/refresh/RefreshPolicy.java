/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.refresh;

import com.ciicgat.sdk.springcache.AbstractCache;

/**
 * @author August
 * @date 2021/11/25 9:39 PM
 */
public abstract class RefreshPolicy {
    private boolean useEqualFunction;

    public boolean isUseEqualFunction() {
        return useEqualFunction;
    }

    public RefreshPolicy setUseEqualFunction(boolean useEqualFunction) {
        this.useEqualFunction = useEqualFunction;
        return this;
    }

    /**
     * 给定缓存及其key，判断是否达到刷新条件。
     *
     * @param cache
     * @param key
     */
    public abstract boolean mayRefresh(boolean global, AbstractCache cache, String key);

    /**
     * 在缓存未命中，初始化缓存后，记录下这次事件。
     *
     * @param cache
     * @param key
     */
    public abstract void recordCacheInit(boolean global, AbstractCache cache, String key);

    public static RefreshPolicy random(double sample) {
        return new RandomRefreshPolicy(sample);
    }

    public static RefreshPolicy frequency(int frequencySeconds) {
        return new FrequencyRefreshPolicy(frequencySeconds);
    }

    public static RefreshPolicy never() {
        return NEVER;
    }

    static final RefreshPolicy NEVER = new RefreshPolicy() {
        @Override
        public boolean mayRefresh(boolean global, AbstractCache cache, String key) {
            return false;
        }

        @Override
        public void recordCacheInit(boolean global, AbstractCache cache, String key) {

        }
    };


}
