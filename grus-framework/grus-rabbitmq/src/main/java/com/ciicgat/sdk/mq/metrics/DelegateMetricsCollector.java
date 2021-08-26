/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.metrics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MetricsCollector;
import com.rabbitmq.client.NoOpMetricsCollector;

/**
 * Created by August.Zhou on 2019-12-09 15:11.
 */
public class DelegateMetricsCollector implements MetricsCollector {
    private MetricsCollector metricsCollector = new NoOpMetricsCollector();

    DelegateMetricsCollector() {
    }

    public DelegateMetricsCollector setMetricsCollector(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
        return this;
    }

    @Override
    public void newConnection(Connection connection) {
        metricsCollector.newConnection(connection);
    }

    @Override
    public void closeConnection(Connection connection) {
        metricsCollector.closeConnection(connection);
    }

    @Override
    public void newChannel(Channel channel) {
        metricsCollector.newChannel(channel);
    }

    @Override
    public void closeChannel(Channel channel) {
        metricsCollector.closeChannel(channel);
    }

    @Override
    public void basicPublish(Channel channel) {
        metricsCollector.basicPublish(channel);
    }

    @Override
    public void consumedMessage(Channel channel, long deliveryTag, boolean autoAck) {
        metricsCollector.consumedMessage(channel, deliveryTag, autoAck);
    }

    @Override
    public void consumedMessage(Channel channel, long deliveryTag, String consumerTag) {
        metricsCollector.consumedMessage(channel, deliveryTag, consumerTag);
    }

    @Override
    public void basicAck(Channel channel, long deliveryTag, boolean multiple) {
        metricsCollector.basicAck(channel, deliveryTag, multiple);
    }

    @Override
    public void basicNack(Channel channel, long deliveryTag) {
        metricsCollector.basicNack(channel, deliveryTag);
    }

    @Override
    public void basicReject(Channel channel, long deliveryTag) {
        metricsCollector.basicReject(channel, deliveryTag);
    }

    @Override
    public void basicConsume(Channel channel, String consumerTag, boolean autoAck) {
        metricsCollector.basicConsume(channel, consumerTag, autoAck);
    }

    @Override
    public void basicCancel(Channel channel, String consumerTag) {
        metricsCollector.basicCancel(channel, consumerTag);
    }

    static DelegateMetricsCollector CONSUMER = new DelegateMetricsCollector();

    static DelegateMetricsCollector PRODUCER = new DelegateMetricsCollector();

    public static DelegateMetricsCollector getConsumerInstance() {
        return CONSUMER;
    }

    public static DelegateMetricsCollector getProducerInstance() {
        return PRODUCER;
    }
}
