/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.opentelemetry;

import com.ciicgat.sdk.util.system.Systems;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by August.Zhou on 2021/12/17 17:53.
 */
public class OpenTelemetrys {
    public static OpenTelemetry getOpenTelemetry() {
        return GlobalOpenTelemetry.get();
    }

    public static ContextPropagators getPropagators() {
        return GlobalOpenTelemetry.get().getPropagators();
    }

    public static TextMapPropagator getTextMapPropagator() {
        return GlobalOpenTelemetry.get().getPropagators().getTextMapPropagator();
    }

    private static final AtomicBoolean SET_STATE = new AtomicBoolean(false);

    public static void set(OpenTelemetry openTelemetry) {
        SET_STATE.set(true);
        GlobalOpenTelemetry.set(openTelemetry);
    }

    public static Tracer getTracer() {
        if (SET_STATE.get()) {
            return GlobalOpenTelemetry.get().getTracer("grus-instrumentation", "1.0.0");
        }
        return OpenTelemetry.noop().getTracer("grus-instrumentation", "1.0.0");
    }


    public static void configSystemTags(Span span) {
        span.setAttribute("app_name", Systems.APP_NAME);
        span.setAttribute("app_instance", Systems.APP_NAME);
        span.setAttribute("env", Systems.APP_INSTANCE);
    }
}
