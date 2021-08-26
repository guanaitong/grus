/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer.debezium.meta;

import java.io.Serializable;

/**
 * Debezium事件内容详情
 * https://debezium.io/documentation/reference/1.5/tutorial.html#viewing-create-event
 * @author wanchongyang
 * @date 2021/5/7 5:38 下午
 */
public class DebeziumRawRecord implements Serializable {
    private DebeziumSchema schema;
    private DebeziumPayload payload;

    public DebeziumSchema getSchema() {
        return schema;
    }

    public void setSchema(DebeziumSchema schema) {
        this.schema = schema;
    }

    public DebeziumPayload getPayload() {
        return payload;
    }

    public void setPayload(DebeziumPayload payload) {
        this.payload = payload;
    }
}
