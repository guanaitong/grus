/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.springframework.cache.Cache;

import java.util.List;

/**
 * Created by August.Zhou on 2023/10/17 20:31
 */
public class Caches {
    private final List<Cache> caches;

    public Caches(List<Cache> caches) {
        this.caches = caches;
    }

    public List<Cache> getCaches() {
        return caches;
    }
}
