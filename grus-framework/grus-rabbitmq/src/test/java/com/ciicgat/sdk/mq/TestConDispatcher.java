/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.lang.threads.Threads;
import com.rabbitmq.client.BuiltinExchangeType;

import java.io.IOException;

/**
 * @Author: yuqi.chen
 * @Date: 2018/6/13
 */
public class TestConDispatcher {

    public static void main(String[] args) throws Exception {


        MsgDispatcher dispatcher = MsgDispatcher
                .newBuilder()
//                .setExchangeNameInfo(exchangeNameInfo)
                .setExchangeName("xxxxy")
//                .setRoutingKey("testRouting.a")
                .setExchangeType(BuiltinExchangeType.DIRECT)
                .build();


        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        try {
                            dispatcher.sendMsg("测试消息a", "a");
//                             System.out.println("123");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
