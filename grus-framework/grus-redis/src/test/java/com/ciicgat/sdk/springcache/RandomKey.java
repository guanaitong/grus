/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by August.Zhou on 2023/10/17 20:37
 */
public class RandomKey implements CacheKey {

    private final String key = RandomStringUtils.randomAlphabetic(4);

    @Override
    public String cacheKey() {
        return key;
    }
}
