/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer.debezium;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.kafka.consumer.KafkaMsgProcessor;
import com.ciicgat.grus.kafka.consumer.debezium.meta.DebeziumPayload;
import com.ciicgat.grus.kafka.consumer.debezium.meta.DebeziumPayloadSource;
import com.ciicgat.grus.kafka.consumer.debezium.meta.DebeziumRawRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多个topic（多表消息）消息处理器
 *
 * @author wanchongyang
 * @date 2021/5/7 4:55 下午
 */
public final class DebeziumMsgProcessor implements KafkaMsgProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumMsgProcessor.class);

    private final DebeziumTableMsgProcessor debeziumTableMsgProcessor;

    public DebeziumMsgProcessor(DebeziumTableMsgProcessor debeziumTableMsgProcessor) {
        this.debeziumTableMsgProcessor = debeziumTableMsgProcessor;
    }

    @Override
    public void handle(ConsumerRecord<String, String> record) {
        String originValue = record.value();
        if (originValue == null) {
            return;
        }

        DebeziumRawRecord debeziumRawRecord;
        try {
            debeziumRawRecord = JSON.parse(originValue, DebeziumRawRecord.class);
        } catch (Exception ex) {
            LOGGER.error("debeziumRawRecord parse error.", ex);
            return;
        }

        DebeziumEventRecord debeziumEventRecord = new DebeziumEventRecord();
        debeziumEventRecord.setRawRecord(debeziumRawRecord);
        DebeziumPayload payload = debeziumRawRecord.getPayload();
        debeziumEventRecord.setEventType(EventType.of(payload.getOp()));
        DebeziumPayloadSource source = payload.getSource();
        debeziumEventRecord.setDbName(source.getDb());
        debeziumEventRecord.setTableName(source.getTable());
        debeziumEventRecord.setMysqlName(source.getName());

        debeziumEventRecord.setBefore(JSON.of(payload.getBefore()));
        debeziumEventRecord.setAfter(JSON.of(payload.getAfter()));

        debeziumTableMsgProcessor.handle(debeziumEventRecord);
    }
}
