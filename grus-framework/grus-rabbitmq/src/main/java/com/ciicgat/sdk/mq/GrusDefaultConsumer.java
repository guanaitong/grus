/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.logger.LogUtil;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.util.system.Systems;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-04-25 19:12.
 */
public class GrusDefaultConsumer extends DefaultConsumer {

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
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusDefaultConsumer.class);

    private final String queue;
    private final AtomicBoolean isRunning;
    private final int maxRetryCount;
    private final boolean fallBack;
    private final Cache<String, AtomicInteger> failedCounts = Caffeine.newBuilder().initialCapacity(16).scheduler(Scheduler.systemScheduler()).expireAfterWrite(Duration.ofMinutes(5)).expireAfterAccess(Duration.ofMinutes(5)).maximumSize(10240).build();
    private MsgProcessor msgProcessor;


    GrusDefaultConsumer(Channel channel, String queue, MsgProcessor msgProcessor, AtomicBoolean isRunning, int maxRetryCount, boolean fallBack) {
        super(channel);
        this.queue = queue;
        this.msgProcessor = msgProcessor;
        this.isRunning = isRunning;
        this.maxRetryCount = maxRetryCount;
        this.fallBack = fallBack;
    }

    @Override
    public final void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (isRunning.get()) {
            handleDelivery0(consumerTag, envelope, properties, body);
        } else {
            // 关机后，消息不再消费，重新入队
            getChannel().basicReject(envelope.getDeliveryTag(), true);
        }
    }

    protected void handleDelivery0(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String msg;
        try {
            msg = new String(body, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("UnsupportedEncodingException", e);
            getChannel().basicAck(envelope.getDeliveryTag(), false);
            return;
        }
        Tracer tracer = OpenTelemetrys.getTracer();

        Context context = OpenTelemetrys.getTextMapPropagator().extract(Context.current(), properties.getHeaders(), getter);

        Span span = tracer.spanBuilder("handleMsg").setSpanKind(SpanKind.CONSUMER).setParent(context).startSpan();
        String traceId = "";
        if (span != Span.getInvalid()) {
            traceId = span.getSpanContext().getTraceId();
            String spanId = span.getSpanContext().getSpanId();
            String parentId = "";
            if (span instanceof ReadWriteSpan readWriteSpan) {
                parentId = readWriteSpan.getParentSpanContext().getSpanId();
            }
            LogUtil.putMdcForTrace(traceId, spanId, parentId, properties.getHeaders().containsKey("uber-trace-id"));
        }

        String msgId = properties.getMessageId();
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            State result;
            try {
                result = msgProcessor.apply(new Message(msgId, envelope.getExchange(), envelope.getRoutingKey(), this.queue, msg));
            } catch (RuntimeException e) {
                LOGGER.error("handle msg error,msg {}", msg, e);
                result = State.Retry;
            }
            if (result == State.OK) {
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            } else if (result == State.Abort) {
                abort(envelope, msgId, msg, body);
            } else if (result == State.Retry) {
                String key = msgId == null ? envelope.getRoutingKey() + ":::" + msg : msgId;
                if (failedCounts.get(key, s -> new AtomicInteger()).incrementAndGet() >= maxRetryCount && envelope.isRedeliver()) {
                    // 超过最大重试次数后，丢弃消息
                    LOGGER.warn("exceed maxRetryCount");
                    abort(envelope, msgId, msg, body);
                    failedCounts.invalidate(key);
                } else {
                    LOGGER.warn("retry msg: {}", msg);
                    getChannel().basicReject(envelope.getDeliveryTag(), true);
                }

            } else {
                LOGGER.warn("result state is null");
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            }
        } finally {
            span.end();
            SlowLogger.logEvent(Module.RABBITMQ, span, "handle msg slow");
        }
    }

    private void abort(Envelope envelope, String msgId, String msg, byte[] body) throws IOException {
        LOGGER.warn("abort msg: {}", msg);
        getChannel().basicReject(envelope.getDeliveryTag(), false);
        if (fallBack) {
            try {
                Map<String, Object> headers = new HashMap<>();
                headers.put("manual", 0);
                headers.put("m-app-name", Systems.APP_NAME);
                headers.put("m-msg-id", msgId);
                headers.put("m-routing-key", envelope.getRoutingKey());
                headers.put("m-exchange", envelope.getExchange());
                headers.put("m-queue", this.queue);
                AMQP.BasicProperties properties = new AMQP.BasicProperties("application/octet-stream", null, headers, 2, 0, null, null, null, null, null, null, null, null, null);
                getChannel().basicPublish(MqConstants.GLOBAL_DEAD_LETTER_EXCHANGE_NAME, "", false, properties, body);
            } catch (Exception e) {
                LOGGER.error("send msg to global error, {}", msg, e);
            }
        }
    }
}
