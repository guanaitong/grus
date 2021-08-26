/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.metrics;

import io.micrometer.core.instrument.MeterRegistry;

public class GlobalMeterRegistry {

    private static MeterRegistry INSTANCE = null;

    public static MeterRegistry get() {
        return INSTANCE;
    }

    public static void register(MeterRegistry meterRegistry) {
        INSTANCE = meterRegistry;
    }
}
