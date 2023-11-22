/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import org.slf4j.MDC;

/**
 * Created by August.Zhou on 2022/10/14 16:34
 */
public class TraceUtil {
    public static final String REQUEST_ID_KEY = "__request_id__";

    public static String getTraceId() {
        return getTraceId(Span.current());
    }

    public static String getTraceId(Span span) {
        if (span != Span.getInvalid()) {
            return span.getSpanContext().getTraceId();
        }
        return "";
    }

    public static String getSpanId() {
        return getSpanId(Span.current());
    }

    public static String getSpanId(Span span) {
        if (span != Span.getInvalid()) {
            return span.getSpanContext().getSpanId();
        }
        return "";
    }

    public static String getParentId() {
        return getParentId(Span.current());
    }

    public static String getParentId(Span span) {
        if (span != Span.getInvalid()) {
            if (span instanceof ReadWriteSpan) {
                SpanContext parentSpanContext = ((ReadWriteSpan) span).getParentSpanContext();
                return parentSpanContext.isValid() ? parentSpanContext.getSpanId() : "";
            }
        }
        return "";
    }

    public static void putMdcForTrace(Span span, String requestId) {
        MDC.put("spanId", getSpanId(span));
        MDC.put("parentId", getParentId(span));
        MDC.put("traceId", getTraceId(span));
        if (requestId != null) {
            MDC.put("req.xRequestId", requestId);
        }
    }

    public static void putMdcForTrace(Span span) {
        putMdcForTrace(span, null);
    }

    public static Context withRequestContext(Context context, String requestId) {
        Baggage baggage = Baggage.builder().put(REQUEST_ID_KEY, requestId).build();
        return baggage.storeInContext(context);
    }

    public static String getRequestIdId(Context context) {
        Baggage baggage = Baggage.fromContextOrNull(context);
        if (baggage == null) {
            return null;
        }
        return baggage.getEntryValue(REQUEST_ID_KEY);
    }
}
