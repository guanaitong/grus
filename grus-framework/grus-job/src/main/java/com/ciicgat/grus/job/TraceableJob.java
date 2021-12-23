/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.job;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Objects;

public class TraceableJob implements SimpleJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceableJob.class);

    private final SimpleJob simpleJob;

    public TraceableJob(SimpleJob simpleJob) {
        this.simpleJob = Objects.requireNonNull(simpleJob);
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        Tracer tracer = OpenTelemetrys.get();
        Span span = tracer.spanBuilder("handleJob").setSpanKind(SpanKind.SERVER).startSpan();
        if (span != Span.getInvalid()) {
            String traceId = span.getSpanContext().getTraceId();
            String spanId = span.getSpanContext().getSpanId();
            String parentId = "";
            if (span instanceof ReadWriteSpan readWriteSpan) {
                parentId = readWriteSpan.getParentSpanContext().getSpanId();
            }
            MDC.put("traceId", traceId);
            MDC.put("spanId", spanId);
            MDC.put("parentId", parentId);
        }

        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            LOGGER.info("JOB_START :{}", shardingContext.getJobName());
            simpleJob.execute(shardingContext);
        } catch (Exception e) {
            LOGGER.error("JOB_EX: {}", shardingContext.getJobName(), e);
            throw e;
        } finally {
            span.end();
            LOGGER.info("JOB_FINISH :{}", shardingContext.getJobName());
        }
    }
}
