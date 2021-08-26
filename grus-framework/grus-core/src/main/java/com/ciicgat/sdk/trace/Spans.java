/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import com.ciicgat.sdk.util.system.Systems;
import io.opentracing.Span;
import io.opentracing.noop.NoopSpan;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by August.Zhou on 2018/8/14 10:18.
 */
public class Spans {


    /**
     * 只有request的产生的span才会是rootSpan
     */
    private static final ThreadLocal<Span> ROOT_SPANS = ThreadLocal.withInitial(() -> NoopSpan.INSTANCE);


    /**
     * 目前只有Web请求和mq的消费，才会作为rootSpan
     *
     * @param span
     */
    public static void setRootSpan(Span span) {
        ROOT_SPANS.set(span);

        MDC.put("traceId", SpanUtil.getTraceId(span));
        MDC.put("spanId", SpanUtil.getSpanId(span));
        MDC.put("parentId", SpanUtil.getParentId(span));
    }

    public static void setRootSpan(Span span, Iterable<Map.Entry<String, String>> baggageItems) {
        setRootSpan(span);

        if (baggageItems == null) {
            return;
        }

        baggageItems.forEach(e -> {
            span.setBaggageItem(e.getKey(), e.getValue());
        });
        MDC.put("req.xRequestId", SpanUtil.getUserId(span));
    }

    public static Span getRootSpan() {
        return ROOT_SPANS.get();
    }

    public static void remove() {
        ROOT_SPANS.remove();
    }


    public static void setSystemTags(Span span) {
        span.setTag("app_name", Systems.APP_NAME);
        span.setTag("app_instance", Systems.APP_INSTANCE);
        span.setTag("env", Systems.WORK_ENV);
    }

    public static void setUserId(String userId) {
        Span span = ROOT_SPANS.get();
        span.setBaggageItem(SpanUtil.USER_ID, userId);
        MDC.put("req.xRequestId", userId);
    }

    /**
     * Set a key:value tag on the Span.
     */
    public static void setTag(String key, String value) {
        Span span = ROOT_SPANS.get();
        span.setTag(key, value);
    }

    /**
     * Same as {@link #setTag(String, String)}, but for boolean values.
     */
    public static void setTag(String key, boolean value) {
        Span span = ROOT_SPANS.get();
        span.setTag(key, value);
    }

    /**
     * Same as {@link #setTag(String, String)}, but for numeric values.
     */
    public static void setTag(String key, Number value) {
        Span span = ROOT_SPANS.get();
        span.setTag(key, value);
    }

    /**
     * Log key:value pairs to the Span with the current walltime timestamp.
     *
     * <p><strong>CAUTIONARY NOTE:</strong> not all Tracer implementations support key:value log fields end-to-end.
     * Caveat emptor.
     *
     * <p>A contrived example (using Guava, which is not required):
     * <pre><code>
     * span.log(
     * ImmutableMap.Builder<String, Object>()
     * .put("event", "soft error")
     * .put("type", "cache timeout")
     * .put("waited.millis", 1500)
     * .build());
     * </code></pre>
     *
     * @param fields key:value log fields. Tracer implementations should support String, numeric, and boolean values;
     *               some may also support arbitrary Objects.
     * @return the Span, for chaining
     * @see Span#log(String)
     */
    public static void log(Map<String, ?> fields) {
        Span span = ROOT_SPANS.get();
        span.log(fields);
    }

    /**
     * Record an event at the current walltime timestamp.
     * <p>
     * Shorthand for
     *
     * <pre><code>
     * span.log(Collections.singletonMap("event", event));
     * </code></pre>
     *
     * @param event the event value; often a stable identifier for a moment in the Span lifecycle
     * @return the Span, for chaining
     */
    public static void log(String event) {
        Span span = ROOT_SPANS.get();
        span.log(event);
    }


}
