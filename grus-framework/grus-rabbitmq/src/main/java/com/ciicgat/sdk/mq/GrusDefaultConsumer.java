/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.sdk.mq.trace.ConsumerSpanDecorator;
import com.ciicgat.sdk.mq.trace.MapHeadersAdapter;
import com.ciicgat.sdk.trace.Spans;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by August.Zhou on 2019-04-25 19:12.
 */
public class GrusDefaultConsumer extends DefaultConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusDefaultConsumer.class);

    private MsgProcessor msgProcessor;
    private final AtomicBoolean isRunning;
    private ConsumerSpanDecorator consumerSpanDecorator = ConsumerSpanDecorator.STANDARD_TAGS;
    private final String host;

    GrusDefaultConsumer(Channel channel, String host, MsgProcessor msgProcessor, AtomicBoolean isRunning) {
        super(channel);
        this.host = host;
        this.msgProcessor = msgProcessor;
        this.isRunning = Objects.requireNonNull(isRunning);
    }

    @Override
    public final void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (isRunning.get()) {
            handleDelivery0(consumerTag, envelope, properties, body);
        } else {
            getChannel().basicReject(envelope.getDeliveryTag(), true);
            // 下面的close可能导致死循环
//            CloseUtils.close(getChannel());
        }
    }

    protected void handleDelivery0(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String text;
        try {
            text = new String(body, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("UnsupportedEncodingException", e);
            getChannel().basicAck(envelope.getDeliveryTag(), false);
            return;
        }

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
            boolean b = msgProcessor.apply(envelope.getRoutingKey(), text);
            if (b) {
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            } else {
                // 消息处理失败，重新入队
                getChannel().basicReject(envelope.getDeliveryTag(), true);
                String msg = msgProcessor.getClass().getName() + "处理失败(将重新入队)[需开发紧急处理]:" + text;
                Alert.send(msg);
                LOGGER.warn(msg);
            }
            consumerSpanDecorator.onResponse(span);
        } catch (Exception e) {
            consumerSpanDecorator.onError(e, span);
            Alert.send("handle msg error,msg:" + text, e);
            LOGGER.error("handle msg error,msg:" + text, e);
            throw e;
        } finally {
            span.finish();
            Spans.remove();
        }
    }
}
