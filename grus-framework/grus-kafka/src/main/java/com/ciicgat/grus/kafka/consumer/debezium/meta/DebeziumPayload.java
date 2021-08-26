/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer.debezium.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;

/**
 * @author wanchongyang
 * @date 2021/5/7 5:39 下午
 */
public class DebeziumPayload implements Serializable {
    private JsonNode before;
    private JsonNode after;
    private DebeziumPayloadSource source;
    private String op;

    @JsonProperty("ts_ms")
    private Long tsMs;

    private Object transaction;

    public JsonNode getBefore() {
        return before;
    }

    public void setBefore(JsonNode before) {
        this.before = before;
    }

    public JsonNode getAfter() {
        return after;
    }

    public void setAfter(JsonNode after) {
        this.after = after;
    }

    public DebeziumPayloadSource getSource() {
        return source;
    }

    public void setSource(DebeziumPayloadSource source) {
        this.source = source;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Long getTsMs() {
        return tsMs;
    }

    public void setTsMs(Long tsMs) {
        this.tsMs = tsMs;
    }

    public Object getTransaction() {
        return transaction;
    }

    public void setTransaction(Object transaction) {
        this.transaction = transaction;
    }
}
