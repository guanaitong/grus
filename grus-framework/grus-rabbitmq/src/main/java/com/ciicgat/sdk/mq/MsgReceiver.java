/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.lang.threads.ShutdownHook;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息接受者。一个exchange只有一个MsgReceiver
 * Created by cpx on 2017-08-07.
 */
public class MsgReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgReceiver.class);
    /**
     * 连接
     */
    private final Connection connection;

    /**
     * 交换机名称
     */
    private final String exchangeName;

    private final int prefetchCount;
    private final List<Queue> queueList;
    private final int parallelNum;
    private final String host;

    private final int maxRetryCount;
    private MsgProcessor msgProcessor;


    /**
     * constructor
     *
     * @param builder
     */
    private MsgReceiver(ReceiverBuilder builder) {
        this.exchangeName = CacheFactory.checkExchangeExisted(builder.exchangeName, true);
        this.queueList = builder.queues;
        this.prefetchCount = builder.prefetchCount;
        this.parallelNum = builder.parallelNum;
        this.host = builder.host;
        this.maxRetryCount = builder.maxRetryCount;
        this.connection = CacheFactory.getConnection(builder, true);
        queueDeclareAndBind();
    }

    public static ReceiverBuilder newBuilder() {
        return new ReceiverBuilder();
    }

    private void queueDeclareAndBind() {
        for (Queue queue : queueList) {
            Channel channel = null;
            try {
                channel = connection.createChannel(); //NOSONAR
                try {
                    AMQP.Queue.DeclareOk result = channel.queueDeclarePassive(queue.getQueueName());
                    LOGGER.warn("queue {} founded,MessageCount {},ConsumerCount {}", queue.getQueueName(), result.getMessageCount(), result.getConsumerCount());
                } catch (IOException e) {
                    LOGGER.warn("queue {} not found", queue.getQueueName());
                    channel = connection.createChannel(); //NOSONAR
                    channel.queueDeclare(queue.getQueueName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), queue.getArgs());
                }
                //
                channel.queueBind(queue.getQueueName(), exchangeName, queue.getRoutingKey());
                if (!queue.isRegisterConsumer()) {
                    // 自动创建死信队列交换机
                    String deadLetterExchange = (String) queue.getArgs().get(MqConstants.DEAD_LETTER_EXCHANGE_KEY);
                    if (deadLetterExchange != null) {
                        channel.exchangeDeclare(deadLetterExchange, BuiltinExchangeType.FANOUT, true); //NOSONAR
                    }
                }
                ConfigData.add(this.exchangeName, queue.getRoutingKey(), queue.getQueueName(), true, channel);
            } catch (IOException e) {
                throw new RabbitMqException(e);
            } finally {
                CloseUtils.close(channel);
            }

        }
    }

    /**
     * 注册消息处理器
     *
     * @param msgProcessor
     * @param
     */
    public void register(MsgProcessor msgProcessor) {
        if (this.msgProcessor != null) {
            throw new UnsupportedOperationException("不支持绑定多个消息处理器");
        }
        this.msgProcessor = Objects.requireNonNull(msgProcessor);
        List<Channel> channelList = new ArrayList<>(parallelNum);
        for (int i = 0; i < parallelNum; i++) {
            try {
                Channel channel = connection.createChannel(); //NOSONAR
                channel.basicQos(prefetchCount);
                channelList.add(channel);
            } catch (IOException e) {
                throw new RabbitMqException(e);
            }
        }


        AtomicBoolean isRunning = new AtomicBoolean(true);

        for (Channel channel : channelList) {
            for (Queue queue : queueList) {
                if (queue.isRegisterConsumer()) {
                    try {
                        channel.basicConsume(queue.getQueueName(), false, new GrusDefaultConsumer(channel, queue.getQueueName(), msgProcessor, isRunning, maxRetryCount, queue.isEnableDeadLetter()));
                    } catch (IOException e) {
                        throw new RabbitMqException(e);
                    }
                }
            }
        }
        ShutdownHook.addShutdownHook(() -> {
            LOGGER.info("close channels");
            isRunning.set(true);
        });
    }

    /**
     * 消息消费者构造器
     */
    public static class ReceiverBuilder extends AbstractBuilder<MsgReceiver, ReceiverBuilder> {


        int prefetchCount = 1;

        /**
         * 最大重试次数，默认3
         */
        int maxRetryCount = 5;


        private List<Queue> queues = new ArrayList<>();


        /**
         * 在精细化定制前,必须填充全部默认值
         */
        private ReceiverBuilder() {
        }


        /**
         * 增加exchange需要绑定的queue，以及与queue相关的路由
         * FANOUT模式下，routingKey为空字符串
         *
         * @param queueName
         * @return
         */
        public ReceiverBuilder addBindQueue(String routingKey, String queueName) {
            return addBindQueue(new Queue(routingKey, queueName));
        }

        public synchronized ReceiverBuilder addBindQueue(Queue queue) {
            for (Queue q : queues) {
                if (q.getQueueName().equals(queue.getQueueName())) {
                    throw new IllegalArgumentException("queue has added");
                }
            }
            queues.add(queue);
            return this;
        }


        /**
         * 设置预取的个数。当parallelNum设置比较大的时候，prefetchCount不易过大。
         * 也就是说，parallelNum*prefetchCount的值不易过大。
         * 当发生消息堆积时，当前queue的unack的消息个数一般等于parallelNum*prefetchCount。
         * 如果过大，那么unack的消息数量也过大，此时会影响mq性能。另外，如果用到优先级队列，那么因为当前客户端预取的过多，优先级队列实际也不会生效。
         *
         * @param prefetchCount
         * @return
         */
        public ReceiverBuilder setPrefetchCount(int prefetchCount) {
            this.prefetchCount = prefetchCount;
            return this;
        }

        /**
         * 消费失败时的最大重试次数
         *
         * @param maxRetryCount
         * @return
         */
        public ReceiverBuilder setMaxRetryCount(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
            return this;
        }

        /**
         * 创建定制化消息分发者
         *
         * @return
         */
        @Override
        public MsgReceiver build() {
            return new MsgReceiver(this);
        }
    }
}
