/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

/**
 * Created by August.Zhou on 2022/7/6 09:56
 */
public class QueueBind {
    /**
     * 队列
     */
    private String queue;
    /**
     * 交换机
     */
    private String exchange;
    /**
     * 路由
     */
    private String routingKey = "";

    public QueueBind() {
    }

    public QueueBind(String queue, String exchange, String routingKey) {
        this.queue = queue;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
