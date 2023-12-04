/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.core.LatencyConfig;
import com.ciicgat.grus.core.LatencyLevel;
import com.ciicgat.grus.core.Module;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-06-26 13:54.
 */
public class SlowMessageLoggerTest {

    @Test
    public void test() {
        Module module = Module.HTTP_CLIENT;
        SlowLogger<String> slowLogger = new SlowLogger<>(module, true, value -> value);
        slowLogger.mayLog(1_000_000L, Map.of("1", 2), "xxxx");
        slowLogger.mayLog(50_000_000L, new HashMap<>(), "xxxx");
        slowLogger.mayLog(60_000_000L, Map.of("1", 2), "xxxx");
        slowLogger.mayLog(300_000_000L, Map.of("1", 2), "xxxx");
        slowLogger.mayLog(350_000_000L, Map.of("1", 2), "xxxx");
        slowLogger.mayLog(2500_000_000L, Map.of("1", 2), "xxxx");
        slowLogger.mayLog(3500_000_000L, Map.of("1", 2), "xxxx");
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
        return LatencyConfig.getModuleConfig("httpclient").getLevel(nanosDuration);
    }
}
