/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.lang.threads.Threads;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通道池的简单高性能实现
 * Created by August.Zhou on 2018-12-28 14:57.
 */
public class ChannelPool implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelPool.class);

    private final Connection connection;

    private final int maxParallelNum;

    private final ConcurrentLinkedQueue<Channel> pool = new ConcurrentLinkedQueue<>();

    private final AtomicInteger current = new AtomicInteger();

    private final boolean confirm;


    public ChannelPool(Connection connection, int maxParallelNum, boolean confirm) {
        this.connection = connection;
        this.maxParallelNum = maxParallelNum;
        this.confirm = confirm;
    }

    public Channel borrowObject() throws IOException {
        while (true) {
            final int value = current.get();
            if (value >= maxParallelNum) {
                Threads.sleep(5);
                continue;
            }
            if (current.compareAndSet(value, 1 + value)) {
                break;
            }
        }
        try {
            return fromPool();
        } catch (IOException e) {
            current.decrementAndGet();
            throw e;
        }
    }

    private Channel fromPool() throws IOException {
        Channel channel = pool.poll();
        if (channel == null) {
            channel = connection.createChannel(); //NOSONAR
            if (confirm) {
                channel.confirmSelect();
            }
            LOGGER.info("created channel {}", channel.getChannelNumber());
        }
        return channel;
    }

    public void returnObject(Channel channel) {
        if (channel == null) {
            return;
        }
        pool.add(channel);
        current.decrementAndGet();
    }

    @Override
    public void close() throws Exception {
        for (Channel channel : pool) {
            channel.close();
        }
    }
}
