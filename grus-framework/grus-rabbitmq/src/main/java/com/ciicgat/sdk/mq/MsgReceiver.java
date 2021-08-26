/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.lang.threads.ShutdownHook;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.util.ComponentStatus;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息接受者
 * Created by cpx on 2017-08-07.
 */
public class MsgReceiver {
    static final int DEFAULT_MESSAGE_TTL = 7 * 24 * 60 * 60 * 1000;
    static final int DEFAULT_QUEUE_TTL = 7 * 24 * 60 * 60 * 1000;
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
    private final List<RQ> rqList;
    private final int parallelNum;
    private final String host;
    private MsgProcessor msgProcessor;


    /**
     * constructor
     *
     * @param builder
     */
    private MsgReceiver(ReceiverBuilder builder) throws Exception {
        this.exchangeName = Objects.requireNonNull(builder.exchangeName, "exchangeName为空");
        this.rqList = builder.rqList;
        this.prefetchCount = builder.prefetchCount;
        this.parallelNum = Optional.ofNullable(builder.parallelNum).orElse(2);
        this.host = builder.host;
        this.connection = CacheConnectionFactory.getConnection(builder, true);
        queueDeclareAndBind();
    }

    public static ReceiverBuilder newBuilder() {
        return new ReceiverBuilder();
    }

    private void queueDeclareAndBind() throws IOException, TimeoutException {
        Map<String, Object> args = new HashMap<>();
        args.put("x-expires", DEFAULT_QUEUE_TTL);
        args.put("x-message-ttl", DEFAULT_MESSAGE_TTL);

        for (RQ rq : rqList) {
            Channel channel = null;
            try {
                channel = connection.createChannel(); //NOSONAR
                try {
                    AMQP.Queue.DeclareOk result = channel.queueDeclarePassive(rq.queueName);
                    LOGGER.warn("queue {} founded,MessageCount {},ConsumerCount {}", rq.queueName, result.getMessageCount(), result.getConsumerCount());
                } catch (IOException e) {
                    LOGGER.warn("queue {} not found", rq.queueName);
                    channel = connection.createChannel(); //NOSONAR
                    channel.queueDeclare(rq.queueName, true, false, false, args);
                }
                //
                channel.queueBind(rq.queueName, exchangeName, rq.routingKey);
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
    public void register(MsgProcessor msgProcessor) throws Exception {
        this.msgProcessor = Objects.requireNonNull(msgProcessor);
        List<Channel> channelList = new ArrayList<>(parallelNum);
        for (int i = 0; i < parallelNum; i++) {
            Channel channel = connection.createChannel(); //NOSONAR
            channel.basicQos(prefetchCount);
            channelList.add(channel);
        }

        boolean isTracing = ComponentStatus.isTraceEnable();

        AtomicBoolean isRunning = new AtomicBoolean(true);

        for (Channel channel : channelList) {
            for (RQ rq : rqList) {
                channel.basicConsume(rq.queueName, false,
                        isTracing ? new GrusTracingConsumer(channel, msgProcessor, host, isRunning) : new GrusDefaultConsumer(channel, msgProcessor, isRunning));
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


        private List<RQ> rqList = new ArrayList<>();


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
            rqList.add(new RQ(routingKey, queueName));
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
         * 创建定制化消息分发者
         *
         * @return
         */
        @Override
        public MsgReceiver build() throws Exception {
            return new MsgReceiver(this);
        }
    }

    private static class RQ {

        /**
         * 队列名称
         */
        String routingKey;

        String queueName;


        RQ(String routingKey, String queueName) {
            this.routingKey = routingKey;
            this.queueName = queueName;
        }
    }
}
