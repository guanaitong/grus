/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import io.opentracing.Span;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by August.Zhou on 2019-06-19 11:13.
 */
public class TraceRunnable implements Runnable {

    private final Span rootSpan = Spans.getRootSpan();
    private final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    private final Runnable runnable;

    public TraceRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        Spans.setRootSpan(rootSpan);
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        this.runnable.run();
    }
}
