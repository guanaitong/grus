/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.main;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.sdk.mq.MsgReceiver;
import com.ciicgat.sdk.mq.State;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: yuqi.chen
 * @Date: 2018/6/13
 */
public class TestReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestReceiver.class);

    public static void main(String[] args) throws Exception {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().build();
        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance());
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();
        OpenTelemetrys.set(sdk);
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


        receiver.register((msg) -> {
            Span span = Span.current();
            String traceId = span.getSpanContext().getTraceId();
            System.out.println(traceId);
//            throw new RuntimeException();
            LOGGER.info(Thread.currentThread().getName() + " m:" + msg);
            System.out.println(Thread.currentThread().getName() + " m:" + msg);
            return State.OK;
        });

//        Threads.sleepSeconds(1000);

        System.out.println("finish");
    }


}
