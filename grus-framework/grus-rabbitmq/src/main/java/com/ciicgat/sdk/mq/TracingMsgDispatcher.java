/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.mq.trace.ProducerSpanDecorator;
import com.ciicgat.sdk.mq.trace.TracingUtils;
import com.ciicgat.sdk.trace.Spans;
import com.rabbitmq.client.AMQP;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

import java.io.IOException;

/**
 * Created by August.Zhou on 2019-04-25 19:05.
 */
class TracingMsgDispatcher extends MsgDispatcher {

    private ProducerSpanDecorator producerSpanDecorator = ProducerSpanDecorator.STANDARD_TAGS;


    TracingMsgDispatcher(DispatcherBuilder dispatcherBuilder) throws Exception {
        super(dispatcherBuilder);
    }

    @Override
    public void sendMsg(String msg, String routingKey, AMQP.BasicProperties properties) throws IOException {
        Tracer tracer = GlobalTracer.get();
        Span rootSpan = Spans.getRootSpan();
        final Span span = tracer.buildSpan("sendMsg")
                .asChildOf(rootSpan == NoopSpan.INSTANCE ? null : rootSpan)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER)
                .start();
        Tags.DB_INSTANCE.set(span, this.host);
        producerSpanDecorator.onRequest(span);
        try {
            AMQP.BasicProperties traceProperties = TracingUtils.inject(properties, span, tracer);
            super.sendMsg(msg, routingKey, traceProperties);
            producerSpanDecorator.onResponse(span);
        } catch (IOException e) {
            producerSpanDecorator.onError(e, span);
            throw e;
        } finally {
            span.finish();
        }
    }
}
