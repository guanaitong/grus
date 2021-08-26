/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * kafka消息处理器
 * @author wanchongyang
 * @date 2021/5/7 4:44 下午
 */
public interface KafkaMsgProcessor {

    /**
     * 该方法是多线程调用，需要保证线程安全
     * @param record
     */
    void handle(ConsumerRecord<String, String> record);
}
