/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.meter;

import com.ciicgat.sdk.springcache.AbstractCache;
import com.ciicgat.sdk.springcache.GrusCacheMeterBinder;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider;

/**
 * Created by August.Zhou on 2021/9/24 12:56.
 */
public class GrusCacheMeterBinderProvider implements CacheMeterBinderProvider<AbstractCache> {
    @Override
    public MeterBinder getMeterBinder(AbstractCache cache, Iterable<Tag> tags) {
        return new GrusCacheMeterBinder(cache, cache.getName(), tags);
    }
}
