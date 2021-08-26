/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.metrics;

import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import io.micrometer.core.instrument.Timer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TelemetryEvent implements Event {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryEvent.class);

    private static final String PREFIX = "grus";

    private static final String SPLIT_SYMBOL = ".";

    private static final String EVENT_NAME_TAG = "event.name";

    private final String eventName;

    private final ModuleEventType moduleEventType;

    private final String[] tags;

    private final String slowDetail;

    private Span span;

    private Timer timer;


    TelemetryEvent(final ModuleEventType moduleEventType, final String eventName, final String[] tags, final String slowDetail) {
        this.moduleEventType = moduleEventType;
        this.eventName = eventName;
        this.tags = tags;
        this.slowDetail = slowDetail;
        this.initAndStart();
    }


    private void initAndStart() {
        // trace
        final Span rootSpan = Spans.getRootSpan();
        final Tracer tracer = GlobalTracer.get();

        // grus.db.request
        final String eventTypeName = PREFIX + SPLIT_SYMBOL + moduleEventType.getModule().getName() + SPLIT_SYMBOL + this.moduleEventType.getOperation();
        span = tracer.buildSpan(eventTypeName)
                .asChildOf(rootSpan == NoopSpan.INSTANCE ? null : rootSpan)
                .withTag(Tags.SPAN_KIND.getKey(), this.moduleEventType.getSpanKind())
                .withTag(EVENT_NAME_TAG, this.eventName)
                .start();

        for (int i = 0; i < tags.length; i++) {
            if (1 == (i & 1)) {
                span.setTag(tags[i], tags[i - 1]);
            }
        }
        Spans.setSystemTags(span);
        // metric
        timer = Timer.builder(eventTypeName)
                .tag(EVENT_NAME_TAG, this.eventName)
                .tags(this.tags)
                .register(GlobalMeterRegistry.get());

    }

    @Override
    public void close() {
        this.span.finish();
        long duration = SpanUtil.getDurationMilliSeconds(span);
        this.timer.record(duration, TimeUnit.MILLISECONDS);
        SlowLogger.logEvent(moduleEventType.getModule(), duration, slowDetail);
    }


    @Override
    public void error(String content, final Throwable throwable) {
        FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, this.moduleEventType.getModule(), content, throwable);
        Tags.ERROR.set(this.span, Boolean.TRUE);
        this.span.log(this.errorLogs(throwable));
    }

    protected Map<String, Object> errorLogs(final Throwable throwable) {
        final Map<String, Object> errorLogs = new HashMap<>(2);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.object", throwable);
        return errorLogs;
    }

}
