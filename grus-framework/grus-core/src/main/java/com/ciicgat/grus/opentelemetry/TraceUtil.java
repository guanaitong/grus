/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.opentelemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import org.slf4j.MDC;

/**
 * Created by August.Zhou on 2022/10/14 16:34
 */
public class TraceUtil {

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
                return ((ReadWriteSpan) span).getParentSpanContext().getSpanId();
            }
        }
        return "";
    }

    public static String putMdcForTrace(Span span, boolean fromJaeger) {
        MDC.put("spanId", getSpanId(span));
        MDC.put("parentId", getParentId(span));
        String traceId = getTraceId(span);
        if (fromJaeger) {
            traceId = removePrefixZeroChar(traceId);
        }
        MDC.put("traceId", traceId);
        return traceId;
    }

    static String removePrefixZeroChar(String text) {
        int i = 0;
        for (int l = text.length(); i < l; ++i) {
            if (text.charAt(i) != '0') {
                break;
            }
        }
        return text.substring(i);
    }
}
