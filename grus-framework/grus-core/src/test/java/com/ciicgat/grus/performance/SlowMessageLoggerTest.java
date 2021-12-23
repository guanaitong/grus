/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.core.LatencyConfig;
import com.ciicgat.grus.core.LatencyLevel;
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
        Assertions.assertFalse(SlowLogger.logEvent(module, 1_000_000L, "xxxx"));
        Assertions.assertFalse(SlowLogger.logEvent(module, 50_000_000L, "xxxx"));
        Assertions.assertFalse(SlowLogger.logEvent(module, 60_000_000L, "xxxx"));
        Assertions.assertFalse(SlowLogger.logEvent(module, 300_000_000L, "xxxx"));
        Assertions.assertFalse(SlowLogger.logEvent(module, 350_000_000L, "xxxx"));
        Assertions.assertTrue(SlowLogger.logEvent(module, 2500_000_000L, "xxxx"));
        Assertions.assertTrue(SlowLogger.logEvent(module, 3500_000_000L, "xxxx"));
    }


    @Test
    public void test1() {
        Module module = Module.HTTP_CLIENT;
        LatencyLevel latencyLevel = LatencyConfig.getModuleConfig(module).getLevel(2500_000_000L);
        Assertions.assertEquals(latencyLevel, LatencyLevel.SLOW);
    }
}
