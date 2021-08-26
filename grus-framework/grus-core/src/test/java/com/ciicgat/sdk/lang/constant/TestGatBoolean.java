/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.constant;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2018-10-19 17:20.
 */
public class TestGatBoolean {

    @Test
    public void test() {

        Assert.assertTrue(GatBoolean.isTrue(1));

        Assert.assertTrue(!GatBoolean.isTrue(2));

        Assert.assertTrue(!GatBoolean.isTrue(3));

        Assert.assertTrue(GatBoolean.isFalse(2));

        Assert.assertTrue(!GatBoolean.isFalse(1));

        Assert.assertTrue(!GatBoolean.isFalse(3));
    }
}
