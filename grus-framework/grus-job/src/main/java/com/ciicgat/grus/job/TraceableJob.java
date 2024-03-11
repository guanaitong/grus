/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.job;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.sdk.trace.TraceUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class TraceableJob implements SimpleJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceableJob.class);

    private final SimpleJob simpleJob;

    public TraceableJob(SimpleJob simpleJob) {
        this.simpleJob = Objects.requireNonNull(simpleJob);
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        Tracer tracer = OpenTelemetrys.getTracer();
        Span span = tracer.spanBuilder("handleJob").setSpanKind(SpanKind.SERVER).startSpan();
        if (span != Span.getInvalid()) {
            TraceUtil.putMdcForTrace(span);
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
