/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2017/7/27 18:04.
 */
public class TestKeyValuePair {

    @Test
    public void test() {
        KeyValuePair<String, String> keyValuePair = new KeyValuePair<>("12312", "saldjf");
        Assertions.assertNotNull(keyValuePair.toString());
    }

}
