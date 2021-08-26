/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.meter;

import com.ciicgat.sdk.util.http.metrics.DelegateEventListener;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;

/**
 * Created by August.Zhou on 2019-12-09 16:11.
 */
public class OkHttpClientMeterBinder implements MeterBinder {
    @Override
    public void bindTo(MeterRegistry registry) {
        OkHttpMetricsEventListener okHttpMetricsEventListenerForCore = OkHttpMetricsEventListener
                .builder(registry, "okhttp-core")
                .uriMapper(request -> request.url().encodedPath())
                .build();
        DelegateEventListener.getForCoreInstance().setEventListener(okHttpMetricsEventListenerForCore);


        OkHttpMetricsEventListener okHttpMetricsEventListenerForFeign = OkHttpMetricsEventListener
                .builder(registry, "okhttp-feign")
                .uriMapper(request -> request.url().encodedPath())
                .build();
        DelegateEventListener.getForFeignInstance().setEventListener(okHttpMetricsEventListenerForFeign);
    }
}
