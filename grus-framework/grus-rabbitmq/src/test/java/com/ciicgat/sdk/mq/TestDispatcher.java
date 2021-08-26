/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.rabbitmq.client.BuiltinExchangeType;

/**
 * @Author: yuqi.chen
 * @Date: 2018/6/13
 */
public class TestDispatcher {

    public static void main(String[] args) throws Exception {


        MsgDispatcher dispatcher = MsgDispatcher
                .newBuilder()
//                .setExchangeNameInfo(exchangeNameInfo)
                .setExchangeName("xxxxy")
//                .setRoutingKey("testRouting.a")
                .setExchangeType(BuiltinExchangeType.DIRECT)
                .build();


        for (int i = 0; i < 100; i++) {
            dispatcher.sendMsg("测试消息a" + i, "a");
            dispatcher.sendMsg("测试消息b" + i, "b");
        }

        System.out.println("finish");
//        System.exit(0);
    }
}
