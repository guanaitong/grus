/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import io.opentracing.Span;
import io.opentracing.noop.NoopSpan;

/**
 * Created by August.Zhou on 2019-03-25 13:49.
 */
public class SpanUtil {

    protected static final String USER_ID = "userid";

    public static long getDurationMilliSeconds(Span span) {
        if (isNoop(span)) {
            return 0;
        }

        if (span instanceof io.jaegertracing.internal.JaegerSpan) {
            return ((io.jaegertracing.internal.JaegerSpan) span).getDuration() / 1000;
        }
        return 0L;
    }

    public static boolean isNoop(Span span) {
        return span instanceof NoopSpan;
    }

    public static String getTraceId(Span span) {
        if (isNoop(span)) {
            return "";
        }
        if (span.context() instanceof io.jaegertracing.internal.JaegerSpanContext) {
            return ((io.jaegertracing.internal.JaegerSpanContext) span.context()).getTraceId();
        }
        return "";
    }

    public static String getSpanId(Span span) {
        if (isNoop(span)) {
            return "";
        }
        if (span.context() instanceof io.jaegertracing.internal.JaegerSpanContext) {
            return Long.toHexString(((io.jaegertracing.internal.JaegerSpanContext) span.context()).getSpanId());
        }
        return "";
    }

    public static String getParentId(Span span) {
        if (isNoop(span)) {
            return "";
        }
        if (span.context() instanceof io.jaegertracing.internal.JaegerSpanContext) {
            return Long.toHexString(((io.jaegertracing.internal.JaegerSpanContext) span.context()).getParentId());
        }
        return "";
    }

    public static String getUserId(Span span) {
        if (isNoop(span)) {
            return "";
        }
        if (span.context() instanceof io.jaegertracing.internal.JaegerSpanContext) {
            String userId = ((io.jaegertracing.internal.JaegerSpanContext) span.context()).getBaggageItem(USER_ID);
            return userId == null ? "" : userId;
        }
        return "";
    }
}
