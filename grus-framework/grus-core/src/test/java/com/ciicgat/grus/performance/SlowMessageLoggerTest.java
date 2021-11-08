/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.core.Module;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2019-06-26 13:54.
 */
public class SlowMessageLoggerTest {

    @Test
    public void test() {
        Module module = Module.HTTP_CLIENT;
        Assertions.assertFalse(SlowLogger.logEvent(module, 1, "xxxx"));
        Assertions.assertFalse(SlowLogger.logEvent(module, 50, "xxxx"));
        Assertions.assertFalse(SlowLogger.logEvent(module, 60, "xxxx"));
        Assertions.assertFalse(SlowLogger.logEvent(module, 300, "xxxx"));
        Assertions.assertTrue(SlowLogger.logEvent(module, 350, "xxxx"));
        Assertions.assertTrue(SlowLogger.logEvent(module, 2500, "xxxx"));
        Assertions.assertTrue(SlowLogger.logEvent(module, 3500, "xxxx"));


    }


    @Test
    public void test1() {
        Module module = Module.HTTP_CLIENT;

        Level level = module.getLevelByDuration(2500);

        boolean alertLevel = level.biggerThan(module.getAlertLevel());

        Assertions.assertTrue(alertLevel);

        level = module.getLevelByDuration(1500);

        alertLevel = level.biggerThan(module.getAlertLevel());

        Assertions.assertFalse(alertLevel);


    }
}
