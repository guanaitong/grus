/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.rabbitmq.processor.MsgProcessor;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import com.ciicgat.sdk.util.system.WorkRegion;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Method;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final Cache<Object, AtomicInteger> failedCounts = Caffeine.newBuilder().initialCapacity(16).scheduler(Scheduler.systemScheduler()).expireAfterWrite(Duration.ofMinutes(5)).expireAfterAccess(Duration.ofMinutes(5)).maximumSize(10240).build();

    public RabbitMqConsumer(Connection connection, int parallel, int prefetch, int maxRetry, boolean fallBack) {
        this.connection = connection;
        this.parallel = parallel;
        this.prefetch = prefetch;
        this.maxRetry = maxRetry;
        this.fallBack = fallBack;
        this.channelList = new ArrayList<>(parallel);
        init();
    }

    private void init() {
        for (int i = 0; i < parallel; i++) {
            try {
                Channel channel = connection.createChannel();
                LOGGER.info("created channel {}", channel.getChannelNumber());
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
            throw new IllegalArgumentException("queue has been registered");
        }
        queues.put(queue, msgProcessor);
        Objects.requireNonNull(queue);
        Objects.requireNonNull(msgProcessor);
        boolean shouldRetry = false;
        try {
            registerToAllChannel(queue, msgProcessor);
        } catch (IOException e) {
            // 在生产环境，如果因为queue不存在，不再抛出异常
            // 开启一个异步线程，每五分钟尝试重新注册msgProcessor
            if (WorkRegion.getCurrentWorkRegion().isProduct() && isQueue404Reason(e)) {
                shouldRetry = true;
                LOGGER.error("register msgProcessor error,queue[{}] not found,", queue, e);
                FrigateNotifier.sendMessageByAppName(NotifyChannel.SMS, Module.RABBITMQ, String.format("生产环境队列[%s]不存在,无法注册消息处理器，请及时处理", queue), null);
            } else {
                throw RabbitMqException.convert(String.format("queue[%s] register msgProcessor failed", queue), e);
            }
        }
        if (shouldRetry) {
            startAsyncRetryTask(queue, msgProcessor);
        }
    }

    private void registerToAllChannel(String queue, MsgProcessor msgProcessor) throws IOException {
        for (Channel channel : channelList) {
            channel.basicConsume(queue, false, new GrusDefaultConsumer(channel, queue, msgProcessor, failedCounts, isRunning, maxRetry, fallBack));
        }
    }

    private void startAsyncRetryTask(String queue, MsgProcessor msgProcessor) {
        Thread thread = Threads.newThread(() -> {
            while (true) {
                Threads.sleepSeconds(300);
                try {
                    registerToAllChannel(queue, msgProcessor);
                    return;
                } catch (IOException e) {
                    LOGGER.error("register msgProcessor error,queue[{}] not found,", queue, e);
                    FrigateNotifier.sendMessageByAppName(NotifyChannel.SMS, Module.RABBITMQ, String.format("生产环境队列[%s]不存在,无法注册消息处理器，请及时处理", queue), null);
                }
            }
        }, "async-register-" + queue, Thread.MIN_PRIORITY);
        thread.start();
    }

    private static boolean isQueue404Reason(IOException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ShutdownSignalException) {
            Method reason = ((ShutdownSignalException) cause).getReason();
            if (reason instanceof AMQP.Channel.Close) {
                int replyCode = ((AMQP.Channel.Close) reason).getReplyCode();
                return replyCode == 404;
            }
        }
        return false;
    }

    @Override
    public void close() {
        isRunning.set(false);
    }
}
