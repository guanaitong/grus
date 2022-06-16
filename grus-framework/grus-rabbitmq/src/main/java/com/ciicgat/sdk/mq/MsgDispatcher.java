/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.ciicgat.sdk.mq.RabbitMqException.handleException;


/**
 * 消息分发者(线程安全的)
 * <p>
 * Created by cpx on 2017-07-31.
 */
public class MsgDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgDispatcher.class);
    private static final TextMapSetter<Map<String, Object>> setter = (carrier, key, value) -> carrier.put(key, value);
    private static final SessionIdGenerator SESSION_ID_GENERATOR = new SessionIdGenerator();
    protected final String host;
    /**
     * 交换机类型
     */
    private final BuiltinExchangeType exchangeType;
    private final boolean confirm;
    /**
     * 交换机名称
     */
    private final String exchangeName;
    /**
     * 连接
     */
    private final Connection connection;
    private final ChannelPool channelPool;

    protected MsgDispatcher(DispatcherBuilder builder) {
        this.exchangeName = CacheFactory.checkExchangeExisted(builder.exchangeName, false);
        this.confirm = builder.confirm;
        this.exchangeType = builder.exchangeType;
        this.connection = CacheFactory.getConnection(builder, false);
        this.host = builder.host;

        try (Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(exchangeName, exchangeType, true); //NOSONAR
        } catch (IOException e) {
            throw new RabbitMqException(e);
        } catch (TimeoutException e) {
            throw new RabbitMqException(e);
        }

        channelPool = new ChannelPool(connection, builder.parallelNum, this.confirm);

        LOGGER.info("exchangeName {}", exchangeName);

    }

    public static DispatcherBuilder newBuilder() {
        return new DispatcherBuilder();
    }

    /**
     * 发送消息，指定路由名称
     *
     * @param msg        消息体
     * @param routingKey 路由
     */
    public void sendMsg(String msg, String routingKey) {
        this.sendMsg(msg, routingKey, MessageProperties.PERSISTENT_BASIC);
    }

    public void sendMsg(String msg, String routingKey, AMQP.BasicProperties properties) {
        if (BuiltinExchangeType.FANOUT == exchangeType) {
            routingKey = "";
        }
        Tracer tracer = OpenTelemetrys.getTracer();
        Span span = tracer.spanBuilder("sendMsg").setSpanKind(SpanKind.PRODUCER).setParent(Context.current()).startSpan();

        Channel channel = null;
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("component", "rabbitmq");
            channel = channelPool.borrowObject();
            Map<String, Object> headers = new HashMap<>();
            //生成消息id
            headers.put(MqConstants.MSG_ID_KEY, SESSION_ID_GENERATOR.generateSessionId());
            OpenTelemetrys.getTextMapPropagator().inject(Context.current(), headers, setter);
            AMQP.BasicProperties traceProperties;
            if (properties == null) {
                traceProperties = new AMQP.BasicProperties("application/octet-stream", null, headers, 2, 0, null, null, null, null, null, null, null, null, null);
            } else {
                if (properties.getHeaders() != null) headers.putAll(properties.getHeaders());
                traceProperties = properties.builder().headers(headers).build();
            }
            channel.basicPublish(exchangeName, routingKey, false, traceProperties, msg.getBytes(StandardCharsets.UTF_8));
            ConfigData.add(this.exchangeName, routingKey, "", false, channel);
            if (this.confirm) {
                channel.waitForConfirmsOrDie();
            }
        } catch (Exception e) {
            LOGGER.error("send msg error,msg {}", msg, e);
            Alert.send("send msg error,msg:" + msg, e);
            handleException(e);
        } finally {
            channelPool.returnObject(channel);
            span.end();
            SlowLogger.logEvent(Module.RABBITMQ, span, "send msg slow");
        }
    }

    /**
     * 消息分发者构造器
     */
    public static class DispatcherBuilder extends AbstractBuilder<MsgDispatcher, DispatcherBuilder> {

        /**
         * 交换机类型
         */
        BuiltinExchangeType exchangeType = BuiltinExchangeType.DIRECT;

        /**
         * 是否启用confirm
         */
        boolean confirm = false;


        /**
         * 在精细化定制前,必须填充全部默认值
         */
        private DispatcherBuilder() {
        }

        /**
         * DIRECT 发送方式
         * 发送端按routing key发送消息，
         * 不同的接收端按不同的routing key接收消息。
         * <p>
         * <p>
         * FANOUT发送方式
         * 发布、订阅模式。
         * 发送端发送广播消息，多个接收端接收。
         * <p>
         * TOPIC发送方式
         * 发送端不只按固定的routing key发送消息，
         * 而是按字符串“匹配”发送，接收端同样如此。
         * 发送消息的routing key不是固定的单词，
         * 而是匹配字符串，如"*.gat.#"，
         * *匹配一个单词，
         * #匹配0个或多个单词。
         */
        public DispatcherBuilder setExchangeType(BuiltinExchangeType exchangeType) {
            this.exchangeType = exchangeType;
            return this;
        }

        public DispatcherBuilder setConfirm(boolean confirm) {
            this.confirm = confirm;
            return this;
        }

        /**
         * 创建定制化消息分发者
         *
         * @return
         */
        @Override
        public MsgDispatcher build() {
            return new MsgDispatcher(this);
        }
    }
}
