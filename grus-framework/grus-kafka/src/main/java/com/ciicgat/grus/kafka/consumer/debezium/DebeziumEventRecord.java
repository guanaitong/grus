/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer.debezium;

import com.ciicgat.grus.kafka.consumer.debezium.meta.DebeziumRawRecord;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * Debezium事件详细内容： https://debezium.io/documentation/reference/1.5/tutorial.html
 *
 * @author wanchongyang
 * @date 2021/5/7 10:28 上午
 */
public class DebeziumEventRecord {
    /**
     * mysql实例名
     * payload.source.name
     */
    private String mysqlName;
    /**
     * 数据库名
     * payload.source.db
     */
    private String dbName;
    /**
     * 数据库表名
     * payload.source.table
     */
    private String tableName;

    /**
     * event之前元数据
     * payload.before
     */
    private Optional<JsonNode> before;

    /**
     * event之后元数据
     * payload.after
     */
    private Optional<JsonNode> after;

    /**
     * 操作类型
     * c: 增
     * u：改
     * d：删
     * payload.op
     */
    private EventType eventType;

    /**
     * 原始消息体
     */
    private DebeziumRawRecord rawRecord;

    public String getMysqlName() {
        return mysqlName;
    }

    public void setMysqlName(String mysqlName) {
        this.mysqlName = mysqlName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Optional<JsonNode> getBefore() {
        return before;
    }

    public void setBefore(Optional<JsonNode> before) {
        this.before = before;
    }

    public Optional<JsonNode> getAfter() {
        return after;
    }

    public void setAfter(Optional<JsonNode> after) {
        this.after = after;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public DebeziumRawRecord getRawRecord() {
        return rawRecord;
    }

    public void setRawRecord(DebeziumRawRecord rawRecord) {
        this.rawRecord = rawRecord;
    }
}
