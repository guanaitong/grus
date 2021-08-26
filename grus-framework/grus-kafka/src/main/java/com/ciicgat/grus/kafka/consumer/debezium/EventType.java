/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer.debezium;

import java.io.Serializable;

/**
 * Debezium事件类型，增删改
 *
 * @author wanchongyang
 * @date 2021/5/7 2:05 下午
 */
public enum EventType implements Serializable {
    /**
     * 新增
     */
    CREATE("c"),
    /**
     * 删除
     */
    DELETE("d"),
    /**
     * 更新
     */
    UPDATE("u"),
    /**
     * 未知
     */
    UNKOWN("");

    private final String opType;

    EventType(String opType) {
        this.opType = opType;
    }

    public String getOpType() {
        return opType;
    }

    public static EventType of(String opType) {
        for (EventType eventType : values()) {
            if (eventType.opType.equals(opType)) {
                return eventType;
            }
        }
        return UNKOWN;
    }

    @Override
    public String toString() {
        return "EventType{" + "opType='" + opType + '\'' + '}';
    }
}
