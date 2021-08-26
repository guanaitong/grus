/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.job;

import com.ciicgat.sdk.trace.Spans;
import io.elasticjob.lite.api.ShardingContext;
import io.elasticjob.lite.api.simple.SimpleJob;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceableJob implements SimpleJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceableJob.class);

    private SimpleJob simpleJob;

    public TraceableJob(SimpleJob simpleJob) {
        this.simpleJob = simpleJob;
    }

    @Override
    public void execute(ShardingContext shardingContext) {

        Tracer tracer = GlobalTracer.get();
        final Span span = tracer.buildSpan(shardingContext.getJobName())
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                .start();

        Spans.setRootSpan(span);

        try {
            LOGGER.info("JOB_START :{}", shardingContext.getJobName());
            simpleJob.execute(shardingContext);
        } catch (Exception e) {
            LOGGER.error("JOB_EX: {}", shardingContext.getJobName(), e);
            Tags.ERROR.set(span, Boolean.TRUE);
            throw e;
        } finally {
            LOGGER.info("JOB_FINISH :{}", shardingContext.getJobName());
            span.finish();
            Spans.remove();
        }
    }
}
