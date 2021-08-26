/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.meter;

import com.ciicgat.grus.metrics.GlobalMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

public class TelemetryMeterBinder implements MeterBinder {
    @Override
    public void bindTo(MeterRegistry registry) {
        GlobalMeterRegistry.register(registry);
    }
}
