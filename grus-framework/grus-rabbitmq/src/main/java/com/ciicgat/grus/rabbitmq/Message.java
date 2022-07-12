/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.json.JSON;

import java.util.Date;
import java.util.Objects;

/**
 * Created by August.Zhou on 2022/7/6 09:40
 */
public class Message {

    private final String exchange;
    private final String routingKey;
    private final String queue;
    private final String msgId;
    private final Date createdAt;
    private final String body;

    /**
     * @param msgId      消息id，发送时产生的。可用于消费时的幂等逻辑。如果业务已经有用于幂等的字段，可以忽略。老版本msgId为null
     * @param routingKey 交换机
     * @param routingKey 路由
     * @param queue      队列
     * @param body       消息内容
     */
    public Message(String exchange, String routingKey, String queue, String msgId, Date createdAt, String body) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.queue = queue;
        this.msgId = msgId;
        this.createdAt = createdAt;
        this.body = body;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getQueue() {
        return queue;
    }

    public String getMsgId() {
        return msgId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(exchange, message.exchange) && Objects.equals(routingKey, message.routingKey) && Objects.equals(queue, message.queue) && Objects.equals(msgId, message.msgId) && Objects.equals(createdAt, message.createdAt) && Objects.equals(body, message.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchange, routingKey, queue, msgId, createdAt, body);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
