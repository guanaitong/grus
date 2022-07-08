/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.rabbitmq.client.AMQP;

/**
 * Created by August.Zhou on 2022/7/8 15:56
 */
public class ExchangePublisher {
    private final RabbitMqProducer rabbitMqProducer;
    private final String exchange;
    /**
     * 默认的路由
     */
    private final String defaultRoutingKey;

    public ExchangePublisher(RabbitMqProducer rabbitMqProducer, String exchange, String defaultRoutingKey) {
        this.rabbitMqProducer = rabbitMqProducer;
        this.exchange = exchange;
        this.defaultRoutingKey = defaultRoutingKey;
    }

    /**
     * 使用默认路由发布消息。调用此方法，defaultRoutingKey不能为空。
     *
     * @param value
     */
    public void publish(Object value) {
        this.publish(this.defaultRoutingKey, value, null);
    }

    public void publish(String routingKey, Object value) {
        this.publish(routingKey, value, null);
    }

    /**
     * 当value是bean时，自动转化为json
     *
     * @param routingKey
     * @param value
     * @param properties
     */
    public void publish(String routingKey, Object value, AMQP.BasicProperties properties) {
        this.rabbitMqProducer.publish(exchange, routingKey, value, properties);
    }
}
