/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;

import com.ciicgat.sdk.lang.exception.CacheDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by August.Zhou on 2017/1/4 14:37.
 */
@ExtendWith(MockitoExtension.class)
public class TestRealCache {


    private PrimaryCache primaryCache;

    @BeforeEach
    public void setup() {
        primaryCache = new PrimaryCache(new LocalCache(10000));
    }


    @Test
    public void test() throws CacheDataException {
        var i = primaryCache.get("123", Person::new);

        var j = primaryCache.get("123", Person::new);

        Assertions.assertSame(i, j);
    }

}
