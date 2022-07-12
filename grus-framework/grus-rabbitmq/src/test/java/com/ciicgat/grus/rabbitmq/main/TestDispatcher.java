/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.main;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.rabbitmq.ExchangePublisher;
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
        ExchangePublisher exchangePublisher = dispatcher.exchangePublisher("grus-test");
//        exchangePublisher.publish("a", "测试消息a");
        for (int i = 0; i < 1000; i++) {
            exchangePublisher.publish("a", "测试消息a" + i);
            exchangePublisher.publish("b", "测试消息b" + i);
        }

        System.out.println("finish");
    }
}
