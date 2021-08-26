/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;

import com.ciicgat.sdk.lang.exception.CacheDataException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by August.Zhou on 2017/1/4 14:37.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TestRealCache {


    private PrimaryCache primaryCache;

    @Before
    public void setup() {
        primaryCache = new PrimaryCache(new LocalCache(10000));
    }


    @Test
    public void test() throws CacheDataException {
        var i = primaryCache.get("123", Person::new);

        var j = primaryCache.get("123", Person::new);

        Assert.assertSame(i, j);
    }

}
