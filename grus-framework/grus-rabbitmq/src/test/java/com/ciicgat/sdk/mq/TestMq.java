/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.rabbitmq.client.BuiltinExchangeType;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2018-11-15 11:13.
 */
public class TestMq {

    private static String forTestExchangeName = "for-test-exchange";

    private static String forTestRoutingKeyA = "for-test-routingKey-A";

    private static String forTestRoutingKeyB = "for-test-routingKey-B";
    @BeforeAll
    public static void registerTracer() {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().build();

        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance());
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));
        GlobalOpenTelemetry.resetForTest();
        OpenTelemetrys.set(sdk);
    }
    @Test
    public void test() throws Exception {
        List<String> msgList = new ArrayList<>();

        int num = 100;
        for (int i = 0; i < num; i++) {
            msgList.add("测试消息:" + Math.random());
        }

        AtomicInteger atomicInteger = new AtomicInteger(num * 2);

        MsgReceiver receiver = MsgReceiver
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .addBindQueue(forTestRoutingKeyA, "a")
                .addBindQueue(forTestRoutingKeyB, "b")
                .setPrefetchCount(1)
                .setParallelNum(4)
                .build();

        List<String> aReceives = new Vector<>();

        List<String> bReceives = new Vector<>();

        receiver.register((r, msg) -> {
            if (forTestRoutingKeyA.equals(r)) {
                aReceives.add(msg);
                atomicInteger.decrementAndGet();
            } else if (forTestRoutingKeyB.equals(r)) {
                bReceives.add(msg);
                atomicInteger.decrementAndGet();
            }
            return true;
        });


        MsgDispatcher dispatcher = MsgDispatcher
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .setExchangeType(BuiltinExchangeType.DIRECT)
                .setParallelNum(2)
                .build();


        for (String s : msgList) {
            dispatcher.sendMsg(s, forTestRoutingKeyA);
            dispatcher.sendMsg(s, forTestRoutingKeyB);
        }

        long start = System.currentTimeMillis();

        while (atomicInteger.get() > 0) {
            if ((System.currentTimeMillis() - start) > 30 * 1000) {
                Assertions.fail("timeout");
                return;
            }
        }
        compare(msgList, aReceives);
        compare(msgList, bReceives);
    }

    private void compare(List<String> a, List<String> b) {
        Assertions.assertEquals(a.size(), b.size());
        List<String> aa = new ArrayList<>(a);
        Collections.sort(aa);

        List<String> bb = new ArrayList<>(b);
        Collections.sort(bb);
        for (int i = 0; i < a.size(); i++) {
            Assertions.assertEquals(aa.get(i), bb.get(i));
        }
    }
}
