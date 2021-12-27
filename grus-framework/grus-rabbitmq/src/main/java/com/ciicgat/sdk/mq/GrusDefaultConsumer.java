/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by August.Zhou on 2019-04-25 19:12.
 */
public class GrusDefaultConsumer extends DefaultConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusDefaultConsumer.class);

    private MsgProcessor msgProcessor;
    private final AtomicBoolean isRunning;
    static final TextMapGetter<Map<String, Object>> getter = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Map<String, Object> basicProperties) {
            return basicProperties.keySet();
        }

        @Override
        public String get(Map<String, Object> basicProperties, String key) {
            if (basicProperties == null) {
                return "";
            }
            Object header = basicProperties.get(key);
            if (header != null) {
                return String.valueOf(header);
            }
            return "";
        }
    };


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
        Tracer tracer = OpenTelemetrys.getTracer();
        Context context = OpenTelemetrys.getTextMapPropagator().extract(Context.current(), properties.getHeaders(), getter);

        Span span = tracer.spanBuilder("handleMsg").setSpanKind(SpanKind.CONSUMER).setParent(context).startSpan();
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
        } catch (Exception e) {
            Alert.send("handle msg error,msg:" + text, e);
            LOGGER.error("handle msg error,msg:" + text, e);
            throw e;
        } finally {
            span.end();
            SlowLogger.logEvent(Module.RABBITMQ, span, "handle msg slow");
        }
    }
}
