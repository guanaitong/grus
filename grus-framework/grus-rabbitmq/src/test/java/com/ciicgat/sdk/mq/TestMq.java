/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.mq.processor.MultiRouteProcessor;
import com.ciicgat.sdk.mq.processor.SingleRouteProcessor;
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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2018-11-15 11:13.
 */
public class TestMq {


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
    public void testNormal() {
        String forTestExchangeName = "for-test-exchange-testNormal";

        MsgDispatcher dispatcher = MsgDispatcher
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .setExchangeType(BuiltinExchangeType.DIRECT)
                .setParallelNum(2)
                .build();

        String forTestRoutingKeyA = "for-test-A" + Math.random();
        String forTestRoutingKeyB = "for-test-B" + Math.random();

        //c abort
        String forTestRoutingKeyC = "for-test-C" + Math.random();


        Set<TestData> msgList = Collections.synchronizedSet(new TreeSet<>());
        int num = 100;
        for (int i = 0; i < num; i++) {
            TestData testData = new TestData();
            testData.setDate(new Date());
            testData.setText("测试消息:" + Math.random());
            testData.setI(i);
            msgList.add(testData);
        }

        AtomicInteger atomicInteger = new AtomicInteger(num * 3);

        MsgReceiver receiver = MsgReceiver
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .addBindQueue(new Queue(forTestRoutingKeyA, forTestRoutingKeyA).setAutoDelete(true).setExclusive(true))
                .addBindQueue(new Queue(forTestRoutingKeyB, forTestRoutingKeyB).setAutoDelete(true).setExclusive(true))
                .addBindQueue(new Queue(forTestRoutingKeyC, forTestRoutingKeyC).setAutoDelete(true).setExclusive(true))
                .setPrefetchCount(1)
                .setParallelNum(4)
                .build();

        Set<TestData> aReceives = Collections.synchronizedSet(new TreeSet<>());
        Set<TestData> bReceives = Collections.synchronizedSet(new TreeSet<>());
        Set<TestData> cReceives = Collections.synchronizedSet(new TreeSet<>());

        Map<QueueKey, SingleRouteProcessor<?>> processors = Map.of(new QueueKey(forTestRoutingKeyA, forTestRoutingKeyA), new SingleRouteProcessor<TestData>() {
            @Override
            protected State handle(Msg msg, TestData object) {
                atomicInteger.decrementAndGet();
                aReceives.add(object);
                return State.OK;
            }
        }, new QueueKey(forTestRoutingKeyB, forTestRoutingKeyB), new SingleRouteProcessor<TestData>() {
            @Override
            protected State handle(Msg msg, TestData object) {
                atomicInteger.decrementAndGet();
                bReceives.add(object);
                return State.OK;
            }
        }, new QueueKey(forTestRoutingKeyC, forTestRoutingKeyC), new SingleRouteProcessor<TestData>() {
            @Override
            protected State handle(Msg msg, TestData object) {
                atomicInteger.decrementAndGet();
                cReceives.add(object);
                return State.Abort;
            }
        });
        receiver.register(new MultiRouteProcessor(processors));


        for (TestData s : msgList) {
            dispatcher.sendMsg(JSON.toJSONString(s), forTestRoutingKeyA);
            dispatcher.sendMsg(JSON.toJSONString(s), forTestRoutingKeyB);
            dispatcher.sendMsg(JSON.toJSONString(s), forTestRoutingKeyC);
        }

        long start = System.currentTimeMillis();

        while (atomicInteger.get() > 0) {
            if ((System.currentTimeMillis() - start) > 30 * 1000) {
                Assertions.fail("timeout");
                return;
            }
        }

        Assertions.assertEquals(msgList.size(), aReceives.size());
        Assertions.assertEquals(msgList.size(), bReceives.size());
        Assertions.assertEquals(msgList.size(), cReceives.size());
    }

    @Test
    public void testRetry() {
        String forTestExchangeName = "for-test-exchange-testRetry";
        MsgDispatcher dispatcher = MsgDispatcher
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .setExchangeType(BuiltinExchangeType.DIRECT)
                .setParallelNum(2)
                .build();

        String forTestRoutingKeyA = "for-test-A" + Math.random();

        Set<TestData> msgList = Collections.synchronizedSet(new TreeSet<>());
        int num = 100;
        for (int i = 0; i < num; i++) {
            TestData testData = new TestData();
            testData.setDate(new Date());
            testData.setText("测试消息:" + Math.random());
            testData.setI(i);
            msgList.add(testData);
        }

        AtomicInteger atomicInteger = new AtomicInteger(num * 1);

        MsgReceiver receiver = MsgReceiver
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .addBindQueue(new Queue(forTestRoutingKeyA, forTestRoutingKeyA).setAutoDelete(true).setExclusive(true))
                .setPrefetchCount(1)
                .setParallelNum(4)
                .build();

        Set<TestData> aReceives = Collections.synchronizedSet(new TreeSet<>());

        Map<QueueKey, SingleRouteProcessor<?>> processors = Map.of(new QueueKey(forTestRoutingKeyA, forTestRoutingKeyA), new SingleRouteProcessor<TestData>() {
            @Override
            protected State handle(Msg msg, TestData object) {
                aReceives.add(object);
                atomicInteger.decrementAndGet();
                if (object.getI() < 50) {
                    throw new RuntimeException();
                }
                return State.Retry;
            }
        });
        receiver.register(new MultiRouteProcessor(processors));


        for (TestData s : msgList) {
            dispatcher.sendMsg(JSON.toJSONString(s), forTestRoutingKeyA);
        }

        long start = System.currentTimeMillis();

        while (atomicInteger.get() >= 0) {
            if ((System.currentTimeMillis() - start) > 30 * 1000) {
                Assertions.fail("timeout");
                return;
            }
        }
    }

    @Test
    public void testDelayDeadLetterTTL() {
        String forTestExchangeName = "for-test-exchange-testDelayDeadLetterTTL";
        MsgDispatcher dispatcher = MsgDispatcher
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .setExchangeType(BuiltinExchangeType.DIRECT)
                .setParallelNum(2)
                .build();

        String forTestRoutingKeyA = "for-test-A" + Math.random();

        Set<TestData> msgList = Collections.synchronizedSet(new TreeSet<>());
        int num = 100;
        for (int i = 0; i < num; i++) {
            TestData testData = new TestData();
            testData.setDate(new Date());
            testData.setText("测试消息:" + Math.random());
            testData.setI(i);
            msgList.add(testData);
        }

        AtomicInteger atomicInteger = new AtomicInteger(num * 1);

        // mainReceiver用于创建一个主交换机，绑定延迟交换机。不需要注册消息处理器，否则消息立即、直接被它给消费了。
        MsgReceiver mainReceiver = MsgReceiver
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .addBindQueue(new Queue(forTestRoutingKeyA, forTestRoutingKeyA).setAutoDelete(true).setExclusive(true).addDelayDeadLetterConfig("dead.ttl.exchange", 3, TimeUnit.SECONDS))
                .setPrefetchCount(1)
                .setParallelNum(1)
                .build();


        // 此receiver才是真正的消费者
        String deadQueue = "dead.ttl.queue." + Math.random();
        MsgReceiver receiver = MsgReceiver
                .newBuilder()
                .setExchangeName("dead.ttl.exchange")
                .addBindQueue(new Queue("", deadQueue).setAutoDelete(true).setExclusive(true))
                .setPrefetchCount(1)
                .setParallelNum(4)
                .build();

        Set<TestData> aReceives = Collections.synchronizedSet(new TreeSet<>());

        Map<QueueKey, SingleRouteProcessor<?>> processors = Map.of(new QueueKey("", deadQueue), new SingleRouteProcessor<TestData>() {
            @Override
            protected State handle(Msg msg, TestData object) {
                aReceives.add(object);
                atomicInteger.decrementAndGet();
                return State.OK;
            }
        });
        receiver.register(new MultiRouteProcessor(processors));


        for (TestData s : msgList) {
            dispatcher.sendMsg(JSON.toJSONString(s), forTestRoutingKeyA);
        }

        Threads.sleepSeconds(4);

        long start = System.currentTimeMillis();

        while (atomicInteger.get() > 0) {
            if ((System.currentTimeMillis() - start) > 30 * 1000) {
                Assertions.fail("timeout");
                return;
            }
        }

        Assertions.assertEquals(msgList.size(), aReceives.size());
    }

    @Test
    public void testGlobalDeadLetterTTL() {
        String forTestExchangeName = "for-test-exchange-testGlobalDeadLetterTTL";
        MsgDispatcher dispatcher = MsgDispatcher
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .setExchangeType(BuiltinExchangeType.DIRECT)
                .setParallelNum(2)
                .build();

        String forTestRoutingKeyA = "for-test-A" + Math.random();

        Set<TestData> msgList = Collections.synchronizedSet(new TreeSet<>());
        int num = 100;
        for (int i = 0; i < num; i++) {
            TestData testData = new TestData();
            testData.setDate(new Date());
            testData.setText("测试消息:" + Math.random());
            testData.setI(i);
            msgList.add(testData);
        }

        AtomicInteger atomicInteger = new AtomicInteger(num * 1);

        MsgReceiver mainReceiver = MsgReceiver
                .newBuilder()
                .setExchangeName(forTestExchangeName)
                .addBindQueue(new Queue(forTestRoutingKeyA, forTestRoutingKeyA).setAutoDelete(true).setExclusive(true).addGlobalDeadLetterConfig())
                .setPrefetchCount(1)
                .setParallelNum(1)
                .build();
        mainReceiver.register(msg -> State.Abort);

        var deadQueue = "global.dead.queue" + Math.random();
        MsgReceiver receiver = MsgReceiver
                .newBuilder()
                .setExchangeName(MqConstants.GLOBAL_DEAD_LETTER_EXCHANGE_NAME)
                .addBindQueue(new Queue("", deadQueue).setAutoDelete(true).setExclusive(true))
                .setPrefetchCount(1)
                .setParallelNum(4)
                .build();

        Set<TestData> aReceives = Collections.synchronizedSet(new TreeSet<>());

        Map<QueueKey, SingleRouteProcessor<?>> processors = Map.of(new QueueKey("", deadQueue), new SingleRouteProcessor<TestData>() {
            @Override
            protected State handle(Msg msg, TestData object) {
                aReceives.add(object);
                atomicInteger.decrementAndGet();
                return State.OK;
            }
        });
        receiver.register(new MultiRouteProcessor(processors));


        for (TestData s : msgList) {
            dispatcher.sendMsg(JSON.toJSONString(s), forTestRoutingKeyA);
        }

        Threads.sleepSeconds(4);

        long start = System.currentTimeMillis();

        while (atomicInteger.get() > 0) {
            if ((System.currentTimeMillis() - start) > 30 * 1000) {
                Assertions.fail("timeout");
                return;
            }
        }

        Assertions.assertTrue(aReceives.size() - msgList.size() >= 0);
    }
}
