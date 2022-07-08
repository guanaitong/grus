/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.core.Stringer;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 消息生产者。全局只有一个RabbitMqConsumer
 * Created by August.Zhou on 2022/7/8 16:02
 */
public class RabbitMqProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqProducer.class);
    private static final TextMapSetter<Map<String, Object>> setter = (carrier, key, value) -> carrier.put(key, value);
    private static final SessionIdGenerator SESSION_ID_GENERATOR = new SessionIdGenerator();

    private final ChannelPool channelPool;

    /**
     * 是否生成消息Id
     */
    private final boolean createMessageId;
    /**
     * 是否生成消息时间戳
     */
    private final boolean createTimestamp;
    /**
     * 是否启用confirm
     */
    private final boolean confirm;

    public RabbitMqProducer(Connection connection, int parallel, boolean confirm, boolean createMessageId, boolean createTimestamp) {
        this.channelPool = new ChannelPool(connection, parallel, confirm);
        this.confirm = confirm;
        this.createMessageId = createMessageId;
        this.createTimestamp = createTimestamp;
    }

    public ExchangePublisher exchangePublisher(String exchange) {
        return new ExchangePublisher(this, exchange);
    }

    /**
     * 发布消息
     *
     * @param exchange   交换机
     * @param routingKey 路由
     * @param value      消息体
     */
    public void publish(String exchange, String routingKey, Object value) {
        this.publish(exchange, routingKey, value, null);
    }

    /**
     * 发布消息
     *
     * @param exchange   交换机
     * @param routingKey 路由
     * @param value      消息体
     * @param properties
     */
    public void publish(String exchange, String routingKey, Object value, AMQP.BasicProperties properties) {
        // value是字符串时，直接使用
        // value继承了Stringer接口时，使用Stringer的string方法。这个主要是方便上游自定义序列化规则
        // 剩下的，value自动转化为json字符串
        String msg = value instanceof String ? (String) value : (value instanceof Stringer ? ((Stringer) value).string() : JSON.toJSONString(value));
        Tracer tracer = OpenTelemetrys.getTracer();
        Span span = tracer.spanBuilder("sendMsg").setSpanKind(SpanKind.PRODUCER).setParent(Context.current()).startSpan();
        Channel channel = null;
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("component", "rabbitmq");
            channel = channelPool.borrowObject();
            Map<String, Object> headers = new HashMap<>();
            //生成消息id
            String messageId = createMessageId ? SESSION_ID_GENERATOR.generateSessionId() : null;
            Date timestamp = createTimestamp ? new Date() : null;
            OpenTelemetrys.getTextMapPropagator().inject(Context.current(), headers, setter);
            AMQP.BasicProperties traceProperties;
            if (properties == null) {
                traceProperties = new AMQP.BasicProperties("application/octet-stream", null, headers, 2, 0, null, null, null, messageId, timestamp, null, null, null, null);
            } else {
                if (properties.getHeaders() != null) headers.putAll(properties.getHeaders());
                traceProperties = properties.builder().headers(headers).messageId(messageId).timestamp(timestamp).build();
            }
            channel.basicPublish(exchange, routingKey, false, traceProperties, msg.getBytes(StandardCharsets.UTF_8));
            if (this.confirm) {
                channel.waitForConfirmsOrDie();
            }
        } catch (Exception e) {
            LOGGER.error("send msg error,msg {}", msg, e);
            Alert.send("send msg error,msg:" + msg, e);
            RabbitMqException.handleException(e);
        } finally {
            channelPool.returnObject(channel);
            span.end();
            SlowLogger.logEvent(Module.RABBITMQ, span, "send msg slow");
        }
    }
}
