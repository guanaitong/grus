/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.processor;

import com.ciicgat.grus.rabbitmq.Message;
import com.ciicgat.grus.rabbitmq.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 批量消息处理器
 * Created by August.Zhou on 2022/7/14 10:42
 */
public abstract class BatchMsgProcessor implements MsgProcessor, AutoCloseable {

    private final AtomicBoolean runningState = new AtomicBoolean(true);
    private LinkedBlockingQueue<Message> cache = new LinkedBlockingQueue<>();
    private final int batchSize;

    private final AtomicLong lastBatchHandleTime = new AtomicLong(System.currentTimeMillis());

    private final long maxInterval;

    public BatchMsgProcessor() {
        this(256, 60);
    }

    public BatchMsgProcessor(int batchSize) {
        this(batchSize, 60);
    }

    /**
     * @param batchSize          批量的size，达到batchSize会触发一次批处理任务
     * @param maxIntervalSeconds 间隔最大多长时间会触发一次批处理任务
     */
    public BatchMsgProcessor(int batchSize, long maxIntervalSeconds) {
        this.batchSize = batchSize;
        this.maxInterval = maxIntervalSeconds * 1000L;
    }

    @Override
    public final State onMessage(Message message) {
        if (runningState.get()) {
            cache.add(message);
            if (cache.size() >= batchSize || (System.currentTimeMillis() - lastBatchHandleTime.get()) >= maxInterval) {
                List<Message> messageList = new ArrayList<>(batchSize);
                cache.drainTo(messageList);
                onBatchMessage(messageList);
                lastBatchHandleTime.set(System.currentTimeMillis());
            }
        } else {
            onBatchMessage(List.of(message));
        }
        return State.OK;
    }

    public abstract void onBatchMessage(List<Message> messageList);

    @Override
    public void close() {
        runningState.set(false);
        List<Message> messageList = new ArrayList<>(batchSize);
        cache.drainTo(messageList);
        if (messageList.size() > 0) {
            onBatchMessage(messageList);
        }
    }
}
