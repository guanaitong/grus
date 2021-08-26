/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.metrics;

import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.util.ComponentStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

public enum ModuleEventType {

    DB_REQUEST(Module.DB, "request", Tags.SPAN_KIND_CLIENT),
    REDIS_REQUEST(Module.REDIS, "request", Tags.SPAN_KIND_CLIENT);

    private Module module;

    private String operation;

    private String spanKind;


    ModuleEventType(Module module, String operation, String spanKind) {
        this.module = module;
        this.operation = operation;
        this.spanKind = spanKind;
    }

    public Module getModule() {
        return module;
    }

    public String getOperation() {
        return operation;
    }

    public String getSpanKind() {
        return spanKind;
    }

    public Event newEvent(String eventName, String[] tags, String slowDetail) {
        if (!ComponentStatus.isTraceEnable()) {
            return Event.NOOP;
        }
        if (!GlobalTracer.isRegistered()) {
            return Event.NOOP;
        }
        MeterRegistry meterRegistry = GlobalMeterRegistry.get();
        if (meterRegistry == null) {
            return Event.NOOP;
        }
        return new TelemetryEvent(this, eventName, tags, slowDetail);
    }
}
