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
    private final String queueName;

    public ConfigData(String exchange, String routingKey, String queueName, boolean consumer) {
        this.appName = Systems.APP_NAME;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.queueName = queueName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigData that = (ConfigData) o;

        if (consumer != that.consumer) return false;
        if (appName != null ? !appName.equals(that.appName) : that.appName != null) return false;
        if (exchange != null ? !exchange.equals(that.exchange) : that.exchange != null) return false;
        if (routingKey != null ? !routingKey.equals(that.routingKey) : that.routingKey != null) return false;
        return queueName != null ? queueName.equals(that.queueName) : that.queueName == null;
    }

    @Override
    public int hashCode() {
        int result = appName != null ? appName.hashCode() : 0;
        result = 31 * result + (consumer ? 1 : 0);
        result = 31 * result + (exchange != null ? exchange.hashCode() : 0);
        result = 31 * result + (routingKey != null ? routingKey.hashCode() : 0);
        result = 31 * result + (queueName != null ? queueName.hashCode() : 0);
        return result;
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

    public String getQueueName() {
        return queueName;
    }
}
