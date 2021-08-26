/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.json.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2018/5/23 15:00.
 */
public class TestProducer1 {

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("mq.servers.dev.ofc");
//        factory.setPort(port);
        factory.setRequestedHeartbeat(10);
//        factory.setUsername("admin");
//        factory.setPassword("jifenpay");
        factory.setUsername("gatmquser");
        factory.setPassword("h7zAeIWqgoeIyM6ANB8u");
        try {

            Connection connection = factory.newConnection();

            Channel channel = connection.createChannel();

            channel.confirmSelect();

            Map<String, String> jsonObject = new HashMap<>();
            jsonObject.put("taskId", System.currentTimeMillis() + "");
            jsonObject.put("method", "GET");
            jsonObject.put("url", "https://www.baidu.com");
            String msg = JSON.toJSONString(jsonObject);
            channel.basicPublish("php.notify", "con", MessageProperties.TEXT_PLAIN, msg.getBytes(StandardCharsets.UTF_8));

            channel.waitForConfirmsOrDie();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
