/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.json.JSONException;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.rabbitmq.processor.MsgProcessor;
import com.ciicgat.sdk.trace.TraceUtil;
import com.ciicgat.sdk.util.system.Systems;
import com.github.benmanes.caffeine.cache.Cache;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-04-25 19:12.
 */
public class GrusDefaultConsumer extends DefaultConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusDefaultConsumer.class);
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


    private static final SlowLogger<String> SLOW_LOGGER = new SlowLogger<>(Module.RABBITMQ + "-consumer", v -> "handle msg slow");

    private final String queue;
    private final AtomicBoolean isRunning;
    private final int maxRetryCount;
    private final boolean fallBack;

    private final MsgProcessor msgProcessor;
    private final Cache<Object, AtomicInteger> failedCounts;

    GrusDefaultConsumer(Channel channel, String queue, MsgProcessor msgProcessor, Cache<Object, AtomicInteger> failedCounts, AtomicBoolean isRunning, int maxRetryCount, boolean fallBack) {
        super(channel);
        this.queue = queue;
        this.msgProcessor = msgProcessor;
        this.failedCounts = failedCounts;
        this.isRunning = isRunning;
        this.maxRetryCount = maxRetryCount;
        this.fallBack = fallBack;
    }

    @Override
    public final void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (isRunning.get()) {
            _handleDelivery(consumerTag, envelope, properties, body);
        } else {
            // 关机后，消息不再消费，重新入队
            getChannel().basicReject(envelope.getDeliveryTag(), true);
        }
    }

    protected void _handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        Tracer tracer = OpenTelemetrys.getTracer();
        Context context = OpenTelemetrys.getTextMapPropagator().extract(Context.current(), properties.getHeaders(), getter);

        Span span = tracer.spanBuilder("consumer").setSpanKind(SpanKind.CONSUMER).setParent(context).startSpan();
        if (span != Span.getInvalid()) {
            TraceUtil.putMdcForTrace(span);
        }
        String msgId = properties.getMessageId();
        try (Scope ignored = context.makeCurrent()) { //将context绑定到全局ContextStorage中
            try (Scope scope = span.makeCurrent()) {
                OpenTelemetrys.configSystemTags(span);
                final Message message = new Message(envelope.getExchange(), envelope.getRoutingKey(), this.queue, msgId, properties.getTimestamp(), body);
                State result;
                try {
                    result = msgProcessor.onMessage(message);
                } catch (JSONException e) {
                    LOGGER.error("handle msg error,msg {}", message, e);
                    result = State.Abort;
                } catch (Exception e) {
                    LOGGER.error("handle msg error,msg {}", message, e);
                    result = State.Retry;
                }
                if (result == State.OK) {
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                    return;
                } else if (result == State.Abort) {
                    abort(envelope, message, body);
                    return;
                } else if (result == State.Retry) {
                    Object key = msgId == null ? message.hashCode() : msgId;
                    int i = failedCounts.get(key, s -> new AtomicInteger()).incrementAndGet();
                    LOGGER.warn("key {} {}", key, i);
                    if (i >= maxRetryCount && envelope.isRedeliver()) {
                        // 超过最大重试次数后，丢弃消息
                        LOGGER.warn("exceed maxRetryCount");
                        abort(envelope, message, body);
                        failedCounts.invalidate(key);
                    } else {
                        LOGGER.warn("retry msg: {}", message);
                        getChannel().basicReject(envelope.getDeliveryTag(), true);
                        return;
                    }

                } else {
                    LOGGER.warn("result state is null");
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                }
            } finally {
                span.end();
                SLOW_LOGGER.mayLog(span, Map.of("queue", queue), "");
            }
        }
    }

    private void abort(Envelope envelope, Message message, byte[] body) throws IOException {
        LOGGER.warn("abort msg: {}", message);
        getChannel().basicReject(envelope.getDeliveryTag(), false);
        if (fallBack) {
            try {
                Map<String, Object> headers = new HashMap<>();
                headers.put("manual", 0);
                headers.put("m-app-name", Systems.APP_NAME);
                headers.put("m-msg-id", message.getMsgId());
                headers.put("m-routing-key", envelope.getRoutingKey());
                headers.put("m-exchange", envelope.getExchange());
                headers.put("m-queue", this.queue);
                if (message.getCreatedAt() != null) headers.put("m-created-at", message.getCreatedAt());
                AMQP.BasicProperties properties = new AMQP.BasicProperties("application/octet-stream", null, headers, 2, 0, null, null, null, null, null, null, null, null, null);
                getChannel().basicPublish(MqConstants.GLOBAL_DEAD_EXCHANGE_NAME, "", false, properties, body);
            } catch (Exception e) {
                LOGGER.error("send msg to global error, {}", message, e);
            }
        }
    }
}
