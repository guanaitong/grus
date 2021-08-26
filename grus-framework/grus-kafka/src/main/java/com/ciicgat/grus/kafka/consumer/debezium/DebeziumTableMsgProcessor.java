/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer.debezium;

/**
 * @author wanchongyang
 * @date 2021/5/7 4:53 下午
 */
public interface DebeziumTableMsgProcessor {

    void handle(DebeziumEventRecord record);
}
