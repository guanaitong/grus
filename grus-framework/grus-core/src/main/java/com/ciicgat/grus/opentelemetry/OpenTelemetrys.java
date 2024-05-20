/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.opentelemetry;

import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkRegion;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.TextMapPropagator;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by August.Zhou on 2021/12/17 17:53.
 */
public class OpenTelemetrys {
    private static final AtomicBoolean STATE = new AtomicBoolean(false);
    private static boolean propagateWithNew = false;
    private static boolean propagateUnSupportedMiddleware = false;

    public static void setPropagateWithNew(boolean propagateWithNew) {
        OpenTelemetrys.propagateWithNew = propagateWithNew;
    }

    public static boolean isPropagateWithNew() {
        return propagateWithNew;
    }

    public static boolean isPropagateUnSupportedMiddleware() {
        return propagateUnSupportedMiddleware;
    }

    public static void setPropagateUnSupportedMiddleware(boolean propagateUnSupportedMiddleware) {
        OpenTelemetrys.propagateUnSupportedMiddleware = propagateUnSupportedMiddleware;
    }


    public static void set(OpenTelemetry openTelemetry) {
        if (STATE.get()) {
            return;
        }
        STATE.set(true);
        GlobalOpenTelemetry.set(openTelemetry);
    }

    public static TextMapPropagator getTextMapPropagator() {
        if (!STATE.get()) {
            return TextMapPropagator.noop();
        }
        return GlobalOpenTelemetry.get().getPropagators().getTextMapPropagator();
    }

    public static Tracer getTracer() {
        if (!STATE.get()) {
            return TracerProvider.noop().get("grus-instrumentation", "1.0.0");
        }
        return GlobalOpenTelemetry.get().getTracer("grus-instrumentation", "1.0.0");
    }


    public static void configSystemTags(Span span) {
        span.setAttribute("app_name", Systems.APP_NAME);
        span.setAttribute("app_instance", Systems.APP_INSTANCE);
        span.setAttribute("env", WorkRegion.getCurrentWorkRegion().getName());
    }
}
