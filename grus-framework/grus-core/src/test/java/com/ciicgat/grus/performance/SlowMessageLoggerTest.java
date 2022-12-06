/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
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
        Assertions.assertEquals(getLatencyLevel(10_000_000L), LatencyLevel.FAST);
        Assertions.assertEquals(getLatencyLevel(199_000_000L), LatencyLevel.FAST);
        Assertions.assertEquals(getLatencyLevel(201_000_000L), LatencyLevel.MEDIUM);
        Assertions.assertEquals(getLatencyLevel(1999_000_000L), LatencyLevel.MEDIUM);
        Assertions.assertEquals(getLatencyLevel(2001_000_000L), LatencyLevel.SLOW);
    }

    private static LatencyLevel getLatencyLevel(final long nanosDuration) {
        return LatencyConfig.getModuleConfig(Module.HTTP_CLIENT).getLevel(nanosDuration);
    }
}
