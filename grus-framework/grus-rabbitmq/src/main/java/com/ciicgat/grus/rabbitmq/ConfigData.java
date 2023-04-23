/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.json.JSON;

/**
 * 应用使用的mq的配置数据
 * Created by August.Zhou on 2022/7/2 22:03
 */
public class ConfigData {
    private final String appName;
    private final String exchange;
    private final String routingKey;
    private final String queue;
    private final boolean consumer;

    public ConfigData(String appName, String exchange, String routingKey, String queue, boolean consumer) {
        this.appName = appName;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.queue = queue;
        this.consumer = consumer;
    }

    public String getAppName() {
        return appName;
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

    public boolean isConsumer() {
        return consumer;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
