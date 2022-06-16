/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.util.system.Systems;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2022/7/2 09:45
 */
public final class Queue {

    /**
     * 路由
     */
    private final String routingKey;

    /**
     * 队列名称
     */
    private final String queueName;
    /**
     * 队列的配置参数
     */
    private final Map<String, Object> args = new HashMap<>();
    /**
     * 是否注册消费者。默认为true。
     * 基于死信队列的延迟队列设置为false.
     */
    private boolean registerConsumer = true;
    /**
     * 是否持久化，默认是
     */
    private boolean durable = true;
    /**
     * 是否链接断开时自动删除，默认否
     */
    private boolean exclusive = false;
    /**
     * 是否自动删除，默认否
     */
    private boolean autoDelete = false;

    public Queue(String routingKey, String queueName) {
        this.routingKey = routingKey;
        this.queueName = queueName;
    }

    /**
     * 支持设置队列参数
     *
     * @param key
     * @return
     */
    public Queue addQueueParam(String key, Object value) {
        this.args.put(key, value);
        return this;
    }

    /**
     * 为了简化大家使用死信队列的逻辑，提供了一个全局的死信队列。
     * 该队列有一个go程序消费这个私死信队列，数据进risk实例的notifyagent.DeadLetter表
     *
     * @return
     */
    public Queue addGlobalDeadLetterConfig() {
        return this.addDeadLetterConfig(MqConstants.GLOBAL_DEAD_LETTER_EXCHANGE_NAME, "");
    }

    /**
     * 设置一个消息会过期的队列
     *
     * @param deadLetterExchange 消息过期后，转发到此exchange。此exchange需要提前创建
     * @param ttl
     * @return
     */
    public Queue addDelayDeadLetterConfig(String deadLetterExchange, int ttl, TimeUnit timeUnit) {
        return addDeadLetterConfig(deadLetterExchange, "").addQueueParam(MqConstants.MESSAGE_TTL_KEY, timeUnit.toMillis(ttl))
                .setRegisterConsumer(false);
    }

    /**
     * 配置队列的死信队列
     *
     * @param deadLetterExchange
     * @param deadLetterRoutingKey
     * @return
     */
    public Queue addDeadLetterConfig(String deadLetterExchange, String deadLetterRoutingKey) {
        return addQueueParam(MqConstants.DEAD_LETTER_EXCHANGE_KEY, deadLetterExchange)
                .addQueueParam(MqConstants.DEAD_LETTER_ROUTING_KEY_KEY, deadLetterRoutingKey);
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public boolean isDurable() {
        return durable;
    }


    public Queue setDurable(boolean durable) {
        this.durable = durable;
        return this;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public Queue setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
        return this;
    }

    public boolean isRegisterConsumer() {
        return registerConsumer;
    }

    public Queue setRegisterConsumer(boolean registerConsumer) {
        this.registerConsumer = registerConsumer;
        return this;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public Queue setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
        return this;
    }

    public boolean isEnableDeadLetter() {
        return args.containsKey(MqConstants.DEAD_LETTER_EXCHANGE_KEY) && !args.containsKey(MqConstants.MESSAGE_TTL_KEY);
    }

    Map<String, Object> getArgs() {
        Map<String, Object> args = new HashMap<>(this.args);
        if (!args.containsKey(MqConstants.EXPIRE_KEY)) {
            args.put(MqConstants.EXPIRE_KEY, MqConstants.DEFAULT_TTL);
        }
        if (!args.containsKey(MqConstants.MESSAGE_TTL_KEY)) {
            args.put(MqConstants.MESSAGE_TTL_KEY, MqConstants.DEFAULT_TTL);
        }
        // 队列上写上应用名字，方便排查
        args.put("x-app-name", Systems.APP_NAME);
        return Collections.unmodifiableMap(args);
    }
}
