/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.main;

import com.ciicgat.grus.rabbitmq.RabbitMqProducer;
import com.ciicgat.grus.rabbitmq.TestConnection;
import com.ciicgat.sdk.lang.threads.Threads;

/**
 * @Author: yuqi.chen
 * @Date: 2018/6/13
 */
public class TestConDispatcher {

    public static void main(String[] args) {

        RabbitMqProducer dispatcher = new RabbitMqProducer(TestConnection.getConnection(), 3, false, true, true);


        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        dispatcher.publish("grus-test", "a", "测试消息a");
                        Threads.sleep(10 * 1000);
                    }

                }
            }).start();
        }

        System.out.println("finish");
    }
}
