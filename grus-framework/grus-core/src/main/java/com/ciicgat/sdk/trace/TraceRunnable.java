/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import com.alibaba.csp.sentinel.context.ContextUtil;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by August.Zhou on 2019-06-19 11:13.
 */
public class TraceRunnable implements Runnable {

    private final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    private final Runnable runnable;
    private final Context context = Context.current();
    private final com.alibaba.csp.sentinel.context.Context sentinelContext = ContextUtil.getContext();


    public TraceRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        if (sentinelContext == null) {
            runWithTrace();
        } else {
            ContextUtil.runOnContext(sentinelContext, this::runWithTrace);
        }
    }

    private void runWithTrace() {
        try (Scope ignored = context.makeCurrent()) {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            runnable.run();
        }
    }
}
