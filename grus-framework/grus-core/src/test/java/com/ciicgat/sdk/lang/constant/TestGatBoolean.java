/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.constant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2018-10-19 17:20.
 */
public class TestGatBoolean {

    @Test
    public void test() {

        Assertions.assertTrue(GatBoolean.isTrue(1));

        Assertions.assertTrue(!GatBoolean.isTrue(2));

        Assertions.assertTrue(!GatBoolean.isTrue(3));

        Assertions.assertTrue(GatBoolean.isFalse(2));

        Assertions.assertTrue(!GatBoolean.isFalse(1));

        Assertions.assertTrue(!GatBoolean.isFalse(3));
    }
}
