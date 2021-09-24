/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.github.benmanes.caffeine.cache.Cache;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;

import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2021/9/24 12:45.
 */
public class GrusCacheMeterBinder extends CacheMeterBinder {

    private AbstractCache<?> cache;

    public GrusCacheMeterBinder(AbstractCache<?> cache, String cacheName, Iterable<Tag> tags) {
        super(cache, cacheName, tags);
        this.cache = cache;
    }

    @Override
    protected Long size() {
        if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
            return ((Cache<?, ?>) cache.getNativeCache()).estimatedSize();
        }
        return null;
    }

    @Override
    protected long hitCount() {
        return cache.stats().hitCount();
    }

    @Override
    protected Long missCount() {
        return cache.stats().missCount();
    }

    @Override
    protected Long evictionCount() {
        return cache.stats().evictionCount();
    }

    @Override
    protected long putCount() {
        return cache.stats().loadCount();
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
        TimeGauge.builder("cache.load.duration", cache, TimeUnit.NANOSECONDS, c -> c.stats().totalLoadTime())
                .tags(getTagsWithCacheName())
                .description("The time the cache has spent loading new values")
                .register(registry);

        FunctionCounter.builder("cache.load", cache, c -> c.stats().loadSuccessCount())
                .tags(getTagsWithCacheName())
                .tags("result", "success")
                .description("The number of times cache lookup methods have successfully loaded a new value")
                .register(registry);

        FunctionCounter.builder("cache.load", cache, c -> c.stats().loadFailureCount())
                .tags(getTagsWithCacheName())
                .tags("result", "failure")
                .description("The number of times {@link Cache} lookup methods failed to load a new value, either " +
                        "because no value was found or an exception was thrown while loading")
                .register(registry);
    }
}
