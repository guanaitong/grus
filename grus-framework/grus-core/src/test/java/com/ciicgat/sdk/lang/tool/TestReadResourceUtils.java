/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by August.Zhou on 2018-10-22 14:22.
 */
public class TestReadResourceUtils {


    @Test
    public void test() {
        Properties properties = ReadResourceUtils.getPropertyFile("test.properties");

        Assert.assertEquals("456", properties.getProperty("age"));
        Assert.assertEquals("August", properties.getProperty("name"));
    }
}
