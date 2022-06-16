/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.main;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.mq.MsgDispatcher;
import com.rabbitmq.client.BuiltinExchangeType;

/**
 * @Author: yuqi.chen
 * @Date: 2018/6/13
 */
public class TestConDispatcher {

    public static void main(String[] args) {


        MsgDispatcher dispatcher = MsgDispatcher.newBuilder()
//                .setExchangeNameInfo(exchangeNameInfo)
                .setExchangeName("xxxxy")
//                .setRoutingKey("testRouting.a")
                .setExchangeType(BuiltinExchangeType.DIRECT).build();


        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        dispatcher.sendMsg("测试消息a", "a");
//                             System.out.println("123");
                        Threads.sleep(10 * 1000);
                    }

                }
            }).start();
            //            dispatcher.sendMsg("测试消息a" + i, "a");
//            dispatcher.sendMsg("测试消息b" + i, "b");
        }

        System.out.println("finish");
//        System.exit(0);
    }
}
