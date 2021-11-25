/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.event;

/**
 * Created by August.Zhou on 2019-08-21 15:10.
 */
public interface CacheChangeListener {
    CacheChangeListener DEFAULT = cacheChangeEvent -> {
    };

    void onChanged(CacheChangeEvent cacheChangeEvent);
}
