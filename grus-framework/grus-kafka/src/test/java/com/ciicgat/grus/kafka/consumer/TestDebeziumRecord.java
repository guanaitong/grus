/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.kafka.consumer.debezium.DebeziumMsgProcessor;
import com.ciicgat.grus.kafka.consumer.debezium.meta.DebeziumRawRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wanchongyang
 * @date 2021/5/8 2:31 下午
 */
public class TestDebeziumRecord {
    @Test
    public void test() {
        String origin = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"struct\",\"fields\":[{\"type\":\"int64\",\"optional\":false,\"field\":\"id\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"jobName\"},{\"type\":\"int16\",\"optional\":false,\"default\":3,\"field\":\"status\"},{\"type\":\"int16\",\"optional\":false,\"default\":-1,\"field\":\"statusCode\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"errorType\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"errorMsg\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"body\"},{\"type\":\"int64\",\"optional\":false,\"name\":\"io.debezium.time.Timestamp\",\"version\":1,\"field\":\"startTime\"},{\"type\":\"int64\",\"optional\":true,\"name\":\"io.debezium.time.Timestamp\",\"version\":1,\"field\":\"completeTime\"}],\"optional\":true,\"name\":\"frigate.gatjob.JobLog.Value\",\"field\":\"before\"},{\"type\":\"struct\",\"fields\":[{\"type\":\"int64\",\"optional\":false,\"field\":\"id\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"jobName\"},{\"type\":\"int16\",\"optional\":false,\"default\":3,\"field\":\"status\"},{\"type\":\"int16\",\"optional\":false,\"default\":-1,\"field\":\"statusCode\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"errorType\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"errorMsg\"},{\"type\":\"string\",\"optional\":false,\"default\":\"\",\"field\":\"body\"},{\"type\":\"int64\",\"optional\":false,\"name\":\"io.debezium.time.Timestamp\",\"version\":1,\"field\":\"startTime\"},{\"type\":\"int64\",\"optional\":true,\"name\":\"io.debezium.time.Timestamp\",\"version\":1,\"field\":\"completeTime\"}],\"optional\":true,\"name\":\"frigate.gatjob.JobLog.Value\",\"field\":\"after\"},{\"type\":\"struct\",\"fields\":[{\"type\":\"string\",\"optional\":false,\"field\":\"version\"},{\"type\":\"string\",\"optional\":false,\"field\":\"connector\"},{\"type\":\"string\",\"optional\":false,\"field\":\"name\"},{\"type\":\"int64\",\"optional\":false,\"field\":\"ts_ms\"},{\"type\":\"string\",\"optional\":true,\"name\":\"io.debezium.data.Enum\",\"version\":1,\"parameters\":{\"allowed\":\"true,last,false\"},\"default\":\"false\",\"field\":\"snapshot\"},{\"type\":\"string\",\"optional\":false,\"field\":\"db\"},{\"type\":\"string\",\"optional\":true,\"field\":\"sequence\"},{\"type\":\"string\",\"optional\":true,\"field\":\"table\"},{\"type\":\"int64\",\"optional\":false,\"field\":\"server_id\"},{\"type\":\"string\",\"optional\":true,\"field\":\"gtid\"},{\"type\":\"string\",\"optional\":false,\"field\":\"file\"},{\"type\":\"int64\",\"optional\":false,\"field\":\"pos\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"row\"},{\"type\":\"int64\",\"optional\":true,\"field\":\"thread\"},{\"type\":\"string\",\"optional\":true,\"field\":\"query\"}],\"optional\":false,\"name\":\"io.debezium.connector.mysql.Source\",\"field\":\"source\"},{\"type\":\"string\",\"optional\":false,\"field\":\"op\"},{\"type\":\"int64\",\"optional\":true,\"field\":\"ts_ms\"},{\"type\":\"struct\",\"fields\":[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"},{\"type\":\"int64\",\"optional\":false,\"field\":\"total_order\"},{\"type\":\"int64\",\"optional\":false,\"field\":\"data_collection_order\"}],\"optional\":true,\"field\":\"transaction\"}],\"optional\":false,\"name\":\"frigate.gatjob.JobLog.Envelope\"},\"payload\":{\"before\":{\"id\":277588952,\"jobName\":\"updateMakeOrderStatusAfterPayHour\",\"status\":3,\"statusCode\":-1,\"errorType\":\"\",\"errorMsg\":\"\",\"body\":\"\",\"startTime\":1619908200000,\"completeTime\":null},\"after\":{\"id\":277588952,\"jobName\":\"updateMakeOrderStatusAfterPayHour\",\"status\":1,\"statusCode\":200,\"errorType\":\"\",\"errorMsg\":\"\",\"body\":\"ok\",\"startTime\":1619908200000,\"completeTime\":1619908200000},\"source\":{\"version\":\"1.5.0.Final\",\"connector\":\"mysql\",\"name\":\"frigate\",\"snapshot\":\"false\",\"db\":\"gatjob\",\"table\":\"JobLog\",\"gtid\":\"cecb0e1d-eceb-11ea-8688-52540a650b54:131217766\",\"file\":\"mysql-bin.005741\",\"pos\":854210,\"row\":0,\"ts_ms\":1619879401000,\"server_id\":1184},\"op\":\"u\",\"ts_ms\":1619879400685}}}";
        DebeziumRawRecord debeziumRawRecord = JSON.parse(origin, DebeziumRawRecord.class);
        Assertions.assertNotNull(debeziumRawRecord);
    }

    @Test
    @Disabled
    public void testConsumer() throws Exception {
        Consumer consumer = Consumer.newBuilder().setPullThreadNum(1)
                .setGroupId("test-consumer-group" + Math.random())
                .setTopics(new String[]{"devapp57.jifenpay.Trade"})
                .build();

        AtomicInteger count = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        DebeziumMsgProcessor debeziumMsgProcessor = new DebeziumMsgProcessor(record -> {
            count.incrementAndGet();
            countDownLatch.countDown();
        });

        consumer.setKafkaMsgProcessor(debeziumMsgProcessor);

        consumer.start();

        countDownLatch.await();
        Assertions.assertTrue(count.get() > 0);
        consumer.close();
    }
}
