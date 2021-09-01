/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.util.system.Systems;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ciicgat.sdk.lang.threads.Threads.LOGGER_UNCAUGHTEXCEPTIONHANDLER;

/**
 * @author wanchongyang
 * @date 2021/5/6 5:23 下午
 */
public class Consumer implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    /**
     * 消费组ID
     */
    private final String groupId;

    /**
     * kafka topic
     */
    private final String[] topics;

    /**
     * 一次调用poll()操作时返回的最大记录数
     */
    private final int maxPollRecords;

    private final List<InnerConsumer> consumers;

    private Executor taskExecutor = Runnable::run;

    private final AtomicBoolean isRunning = new AtomicBoolean();
    private KafkaMsgProcessor kafkaMsgProcessor;
    private final int pullThreadNum;

    private Consumer(String groupId, int maxPollRecords, int pullThreadNum, String[] topics) {
        this.groupId = groupId;
        this.maxPollRecords = maxPollRecords;
        this.topics = topics;
        this.pullThreadNum = pullThreadNum;
        this.consumers = new ArrayList<>(pullThreadNum);

        for (int i = 0; i < pullThreadNum; i++) {
            consumers.add(new InnerConsumer(i));
        }
    }

    public static KafkaConsumerBuilder newBuilder() {
        return new KafkaConsumerBuilder();
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setKafkaMsgProcessor(KafkaMsgProcessor kafkaMsgProcessor) {
        this.kafkaMsgProcessor = Objects.requireNonNull(kafkaMsgProcessor);
    }

    public void start() {
        isRunning.set(true);
        for (int i = 0; i < pullThreadNum; i++) {
            consumers.get(i).start();
        }
        LOGGER.info("started.");
    }

    @Override
    public void close() throws Exception {
        isRunning.set(false);
        for (int i = 0; i < pullThreadNum; i++) {
            consumers.get(i).close();
        }
        LOGGER.info("closed.");
    }

    private KafkaConsumer<String, String> initKafkaConsumer() {
        Properties consumerProperties = getDefaultConsumerProperties();
        // group.id 消费组id
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, String.valueOf(maxPollRecords));

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
        consumer.subscribe(Arrays.asList(topics));

        return consumer;
    }

    private Properties getDefaultConsumerProperties() {
        Properties properties = new Properties();
        // bootstrap.servers kafka集群地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        // key.deserializer 消息key序列化方式
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // value.deserializer 消息体序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // enable.auto.commit 设置自动提交offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(false));
        // auto.offset.reset
        // earliest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
        // latest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
        // none：topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    private String getBootstrapServers() {
        Properties properties = GlobalGconfConfig.getConfig().getProperties("address.properties");
        return properties.getProperty("kafka.bootstrap.servers");
    }

    private class InnerConsumer extends Thread implements AutoCloseable {
        KafkaConsumer<String, String> kafkaConsumer;

        InnerConsumer(int i) {
            super("kafka_consumer_" + i);
            this.setPriority(Thread.NORM_PRIORITY);
            this.setUncaughtExceptionHandler(LOGGER_UNCAUGHTEXCEPTIONHANDLER);
            kafkaConsumer = initKafkaConsumer();
        }

        @Override
        public void run() {
            while (isRunning.get()) {
                try {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(3_000));
                    if (!records.isEmpty()) {
                        final CountDownLatch countDownLatch = new CountDownLatch(records.count());
                        for (ConsumerRecord<String, String> record : records) {
                            taskExecutor.execute(() -> {
                                try {
                                    kafkaMsgProcessor.handle(record);
                                } catch (Exception ex) {
                                    LOGGER.error("current record:{}", record);
                                    LOGGER.error("kafkaMsgProcessor handle error.", ex);
                                }
                                countDownLatch.countDown();
                            });
                        }
                        countDownLatch.await();
                        kafkaConsumer.commitSync();
                    }
                } catch (Exception ex) {
                    LOGGER.error("consumer records error.", ex);
                    Threads.sleep(200);
                }
            }
        }

        @Override
        public void close() throws Exception {
            // 在主线程中等待InnerConsumer线程跳出while true循环
            join();
            CloseUtils.close(kafkaConsumer);
        }
    }

    public static class KafkaConsumerBuilder {
        /**
         * 消费者所属消费组的唯一标识
         */
        private String groupId = Systems.APP_NAME;
        /**
         * topic集合
         */
        private String[] topics;
        /**
         * 线程池大小
         */
        private int pullThreadNum = 1;

        /**
         * 一次调用poll()操作时返回的最大记录数，kafka默认值为500
         */
        private int maxPollRecords = 200;

        private KafkaConsumerBuilder() {
        }

        public KafkaConsumerBuilder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public KafkaConsumerBuilder setTopics(String[] topics) {
            this.topics = topics;
            return this;
        }

        public KafkaConsumerBuilder setPullThreadNum(int pullThreadNum) {
            this.pullThreadNum = pullThreadNum;
            return this;
        }

        public KafkaConsumerBuilder setMaxPollRecords(int maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
            return this;
        }

        public Consumer build() {
            return new Consumer(groupId, maxPollRecords, pullThreadNum, topics);
        }
    }

}
