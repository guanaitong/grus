/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.opentelemetry;

import com.ciicgat.sdk.util.system.Systems;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

/**
 * Created by August.Zhou on 2021/12/17 17:53.
 */
public class OpenTelemetrys {
    public static Tracer get() {
        return GlobalOpenTelemetry.get().getTracer("grus-instrumentation", "1.0.0");
    }

    public static void initFoTest() {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().addSpanProcessor(SimpleSpanProcessor.create(new LoggingSpanExporter())).build();

        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance());
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));
        GlobalOpenTelemetry.set(sdk);
    }

    public static void configSystemTags(Span span) {
        span.setAttribute("app_name", Systems.APP_NAME);
        span.setAttribute("app_instance", Systems.APP_NAME);
        span.setAttribute("env", Systems.APP_INSTANCE);
    }
}
