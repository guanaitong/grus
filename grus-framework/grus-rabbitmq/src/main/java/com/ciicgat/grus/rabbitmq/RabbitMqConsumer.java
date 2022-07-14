/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.rabbitmq.processor.MsgProcessor;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消费者。全局只有一个RabbitMqConsumer
 * Created by August.Zhou on 2022/7/8 16:02
 */
public class RabbitMqConsumer implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConsumer.class);
    /**
     * 连接
     */
    private final Connection connection;


    /**
     * 最大并发数
     */
    private final int parallel;

    /**
     * 预拉取数量
     */
    private final int prefetch;

    /**
     * 最大重试次数
     */
    private final int maxRetry;

    private final boolean fallBack;
    private final List<Channel> channelList;

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private final ConcurrentMap<String, MsgProcessor> queues = new ConcurrentHashMap<>();


    public RabbitMqConsumer(Connection connection, int parallel, int prefetch, int maxRetry, boolean fallBack) {
        this.connection = connection;
        this.parallel = parallel;
        this.prefetch = prefetch;
        this.maxRetry = maxRetry;
        this.fallBack = fallBack;
        this.channelList = new ArrayList<>(parallel);
        init();
    }

    public void init() {
        for (int i = 0; i < parallel; i++) {
            try {
                Channel channel = connection.createChannel();
                channel.basicQos(prefetch);
                channelList.add(channel);
            } catch (IOException e) {
                throw new RabbitMqException(e);
            }
        }
    }

    /**
     * 注册消息处理器
     *
     * @param msgProcessor
     * @param
     */
    public synchronized void consume(String queue, MsgProcessor msgProcessor) {
        if (queues.containsKey(queue)) {
            throw new IllegalArgumentException("queue existed");
        }
        queues.put(queue, msgProcessor);
        Objects.requireNonNull(queue);
        Objects.requireNonNull(msgProcessor);
        for (Channel channel : channelList) {
            try {
                channel.basicConsume(queue, false, new GrusDefaultConsumer(channel, queue, msgProcessor, isRunning, maxRetry, fallBack));
            } catch (IOException e) {
                throw new RabbitMqException(String.format("queue[%s] consume failed", queue), e);
            }
        }
    }

    @Override
    public void close() {
        isRunning.set(false);
        for (Map.Entry<String, MsgProcessor> processorEntry : queues.entrySet()) {
            if (processorEntry.getValue() instanceof AutoCloseable) {
                CloseUtils.close(processorEntry.getValue());
            }
        }
    }
}
