/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.mq.trace.MapHeadersAdapter;
import com.ciicgat.sdk.mq.trace.TracingUtils;
import com.ciicgat.sdk.util.system.Systems;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.RateLimitingSampler;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

public class TestTracing {

    @BeforeClass
    public static void registerJaegerTracer() {
        Configuration.SenderConfiguration senderConfiguration =
                new Configuration
                        .SenderConfiguration()
                        .withAgentHost("127.0.0.1")
                        .withAgentPort(6831);

        Configuration.ReporterConfiguration reporterConfig =
                new Configuration
                        .ReporterConfiguration()
                        .withSender(senderConfiguration)
                        .withLogSpans(false);

        Float parm = "unknown".equals(Systems.APP_NAME) ? 0f : 50f;
        //采样配置
        Configuration.SamplerConfiguration samplerConfig =
                new Configuration
                        .SamplerConfiguration()
                        .withType(RateLimitingSampler.TYPE)
                        .withParam(parm);

        JaegerTracer tracer = new Configuration(Systems.APP_NAME).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
        GlobalTracer.registerIfAbsent(tracer);
    }

    @Test
    public void testInjectExtract() {
        Tracer tracer = GlobalTracer.get();

        Span span = tracer.buildSpan("sendMsg")
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER)
                .start();

        AMQP.BasicProperties properties = TracingUtils.inject(MessageProperties.PERSISTENT_BASIC, span, tracer);

        Map<String, Object> headerMap = properties.getHeaders();

        String traceHeader = String.valueOf(headerMap.get("uber-trace-id"));
        Assert.assertNotNull(traceHeader);

        SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new MapHeadersAdapter(properties.getHeaders()));
        Assert.assertTrue(traceHeader.contains(spanContext.toTraceId()));
    }

}
