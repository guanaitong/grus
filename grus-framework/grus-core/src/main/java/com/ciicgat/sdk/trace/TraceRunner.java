/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

/**
 * Created by August.Zhou on 2023/11/24 11:31
 */
public class TraceRunner {

    /**
     * @param spanName 传一个固定的，有意义的名字
     * @param runnable
     */
    public static void startSpanAndRun(String spanName, Runnable runnable) {
        Tracer tracer = OpenTelemetrys.getTracer();
        Span span = tracer.spanBuilder(spanName).setSpanKind(SpanKind.INTERNAL).startSpan();
        if (span != Span.getInvalid()) {
            TraceUtil.putMdcForTrace(span);
        }
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            runnable.run();
        } finally {
            span.end();
        }
    }
}
