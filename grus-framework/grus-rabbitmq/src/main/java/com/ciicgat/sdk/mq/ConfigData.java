/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.util.system.Systems;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.ciicgat.sdk.mq.MqConstants.GLOBAL_CONFIG_EXCHANGE_NAME;

/**
 * 应用使用的mq的配置数据
 * Created by August.Zhou on 2022/7/2 22:03
 */
public class ConfigData {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigData.class);
    private static Set<ConfigData> dataSet = ConcurrentHashMap.newKeySet();
    private final String appName;
    private final boolean consumer;
    private final String exchange;
    private final String routingKey;
    private final String queue;

    public ConfigData(String exchange, String routingKey, String queue, boolean consumer) {
        this.appName = Systems.APP_NAME;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.queue = queue;
        this.consumer = consumer;
    }

    public static void add(String exchange, String routingKey, String queueName, boolean consumer, Channel channel) {
        ConfigData configData = new ConfigData(exchange, routingKey, queueName, consumer);
        if (dataSet.contains(configData)) {
            return;
        }
        boolean added = dataSet.add(configData);
        if (added) {
            try {
                channel.basicPublish(GLOBAL_CONFIG_EXCHANGE_NAME, "", false, MessageProperties.PERSISTENT_BASIC, configData.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                LOGGER.error("send error", e);
            }
        }
    }

    public String getQueue() {
        return queue;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getAppName() {
        return appName;
    }

    public boolean isConsumer() {
        return consumer;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigData that = (ConfigData) o;
        return consumer == that.consumer && Objects.equals(appName, that.appName) && Objects.equals(exchange, that.exchange) && Objects.equals(routingKey, that.routingKey) && Objects.equals(queue, that.queue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, consumer, exchange, routingKey, queue);
    }
}
