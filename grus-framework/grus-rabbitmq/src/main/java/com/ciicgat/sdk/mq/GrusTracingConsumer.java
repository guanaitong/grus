/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.mq.trace.ConsumerSpanDecorator;
import com.ciicgat.sdk.mq.trace.MapHeadersAdapter;
import com.ciicgat.sdk.trace.Spans;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by August.Zhou on 2019-04-25 19:12.
 */
class GrusTracingConsumer extends GrusDefaultConsumer {

    private ConsumerSpanDecorator consumerSpanDecorator = ConsumerSpanDecorator.STANDARD_TAGS;

    private String host;

    GrusTracingConsumer(Channel channel, MsgProcessor msgProcessor, String host, AtomicBoolean isRunning) {
        super(channel, msgProcessor, isRunning);
        this.host = host;
    }

    @Override
    protected void handleDelivery0(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        Tracer tracer = GlobalTracer.get();
        SpanContext spanContext = null;
        if (properties.getHeaders() != null) {
            spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new MapHeadersAdapter(properties.getHeaders()));
        }
        final Span span = tracer.buildSpan("handleMsg")
                .asChildOf(spanContext)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CONSUMER)
                .start();
        Spans.setRootSpan(span);

        Tags.DB_INSTANCE.set(span, this.host);

        consumerSpanDecorator.onRequest(span);

        try {
            super.handleDelivery0(consumerTag, envelope, properties, body);
            consumerSpanDecorator.onResponse(span);
        } catch (IOException e) {
            consumerSpanDecorator.onError(e, span);
        } finally {
            span.finish();
            Spans.remove();
        }
    }
}
