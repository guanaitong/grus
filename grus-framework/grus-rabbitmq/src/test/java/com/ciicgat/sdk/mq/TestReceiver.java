/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.lang.threads.Threads;

/**
 * @Author: yuqi.chen
 * @Date: 2018/6/13
 */
public class TestReceiver {

    public static void main(String[] args) throws Exception {

//        ExchangeNameInfo exchangeNameInfo = new ExchangeNameInfo();
//        exchangeNameInfo.setBizName("test");
//        exchangeNameInfo.setSiteName("test");
//        QueueNameInfo queueNameInfo = new QueueNameInfo();
//        queueNameInfo.setAppId("9999");
//        queueNameInfo.setExchangeName();

        MsgReceiver receiver = MsgReceiver
                .newBuilder()
                .setExchangeName("xxxxy")
                .addBindQueue("a", "a")
                .addBindQueue("b", "b")
                .setPrefetchCount(1)
                .setParallelNum(10).build();


        receiver.register((r, msg) -> {

            System.out.println(Thread.currentThread().getName() + " r:" + r + "   _____" + msg + " " + msg.contains(r));
            Threads.sleepSeconds(5);
            return true;
        });

        System.out.println("finish");
    }


}
