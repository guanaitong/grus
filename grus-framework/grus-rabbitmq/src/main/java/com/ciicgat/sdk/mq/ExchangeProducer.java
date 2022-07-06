/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.rabbitmq.client.AMQP;

/**
 * Created by August.Zhou on 2022/7/8 15:56
 */
public class ExchangeProducer {
    private final RabbitMqProducer rabbitMqProducer;
    private final String exchange;

    public ExchangeProducer(RabbitMqProducer rabbitMqProducer, String exchange) {
        this.rabbitMqProducer = rabbitMqProducer;
        this.exchange = exchange;
    }

    public void publish(String routingKey, String msg) {
        this.publish(routingKey, msg, null);
    }

    public void publish(String routingKey, String msg, AMQP.BasicProperties properties) {
        this.rabbitMqProducer.publish(exchange, routingKey, msg, properties);
    }
}
