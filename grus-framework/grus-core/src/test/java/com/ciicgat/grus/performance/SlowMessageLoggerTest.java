/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.core.Module;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2019-06-26 13:54.
 */
public class SlowMessageLoggerTest {

    @Test
    public void test() {
        Module module = Module.HTTP_CLIENT;
        Assert.assertFalse(SlowLogger.logEvent(module, 1, "xxxx"));
        Assert.assertFalse(SlowLogger.logEvent(module, 50, "xxxx"));
        Assert.assertFalse(SlowLogger.logEvent(module, 60, "xxxx"));
        Assert.assertFalse(SlowLogger.logEvent(module, 300, "xxxx"));
        Assert.assertTrue(SlowLogger.logEvent(module, 350, "xxxx"));
        Assert.assertTrue(SlowLogger.logEvent(module, 2500, "xxxx"));
        Assert.assertTrue(SlowLogger.logEvent(module, 3500, "xxxx"));


    }


    @Test
    public void test1() {
        Module module = Module.HTTP_CLIENT;

        Level level = module.getLevelByDuration(2500);

        boolean alertLevel = level.biggerThan(module.getAlertLevel());

        Assert.assertTrue(alertLevel);

        level = module.getLevelByDuration(1500);

        alertLevel = level.biggerThan(module.getAlertLevel());

        Assert.assertFalse(alertLevel);


    }
}
