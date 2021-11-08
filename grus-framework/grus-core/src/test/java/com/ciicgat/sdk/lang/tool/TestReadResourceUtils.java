/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * Created by August.Zhou on 2018-10-22 14:22.
 */
public class TestReadResourceUtils {


    @Test
    public void test() {
        Properties properties = ReadResourceUtils.getPropertyFile("test.properties");

        Assertions.assertEquals("456", properties.getProperty("age"));
        Assertions.assertEquals("August", properties.getProperty("name"));
        Properties properties2 = ReadResourceUtils.getPropertyFile("test.properties");
        Assertions.assertEquals("456", properties2.getProperty("age"));
        Assertions.assertEquals("August", properties2.getProperty("name"));
    }
}
