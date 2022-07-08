/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.main;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.rabbitmq.RabbitMqProducer;
import com.ciicgat.grus.rabbitmq.TestConnection;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;

/**
 * @Author: yuqi.chen
 * @Date: 2018/6/13
 */
public class TestDispatcher {

    public static void main(String[] args) {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().build();
        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance());
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();
        OpenTelemetrys.set(sdk);

        RabbitMqProducer dispatcher = new RabbitMqProducer(TestConnection.getConnection(), 3, false, true, true);
//        MsgDispatcher dispatcher = MsgDispatcher
//                .newBuilder()
////                .setExchangeNameInfo(exchangeNameInfo)
//                .setExchangeName("xxxxy")
////                .setRoutingKey("testRouting.a")
//                .setExchangeType(BuiltinExchangeType.DIRECT)
//                .build();

        dispatcher.exchangeObjectPublisher("xxxxy").publish("a", "测试消息a");
//        for (int i = 0; i < 100; i++) {
//            dispatcher.sendMsg("测试消息a" + i, "a");
//            dispatcher.sendMsg("测试消息b" + i, "b");
//        }

        System.out.println("finish");
//        System.exit(0);
    }
}
