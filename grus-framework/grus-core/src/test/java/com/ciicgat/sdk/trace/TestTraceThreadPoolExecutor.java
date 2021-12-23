/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2019-01-08 12:58.
 */
public class TestTraceThreadPoolExecutor {


    @BeforeAll
    public static void registerTracer() {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().build();

        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance());
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));
        GlobalOpenTelemetry.resetForTest();
        GlobalOpenTelemetry.set(sdk);
    }


    @Test
    public void test() throws ExecutionException, InterruptedException {

        testRunnable(new TraceThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>()));
        testRunnable(TraceThreadPoolExecutor.wrap(Executors.newSingleThreadExecutor()));

        testCallable(new TraceThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>()));
        testCallable(TraceThreadPoolExecutor.wrap(Executors.newCachedThreadPool()));

    }

    public void testRunnable(ExecutorService executorService) throws InterruptedException {
        Tracer tracer = OpenTelemetrys.get();
        Span span = tracer.spanBuilder("main").setSpanKind(SpanKind.INTERNAL).startSpan();
        try (Scope ignored = span.makeCurrent()) {
            String spanId = span.getSpanContext().getSpanId();
            Assertions.assertNotNull(spanId);
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Assertions.assertEquals(spanId, Span.current().getSpanContext().getSpanId());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            executorService.shutdown();
        }
    }


    public void testCallable(ExecutorService executorService) throws ExecutionException, InterruptedException {
        Tracer tracer = OpenTelemetrys.get();
        Span span = tracer.spanBuilder("main").setSpanKind(SpanKind.INTERNAL).startSpan();
        try (Scope ignored = span.makeCurrent()) {
            String spanId = span.getSpanContext().getSpanId();
            Assertions.assertNotNull(spanId);
            Callable<Integer> callable = () -> {
                Assertions.assertEquals(spanId, Span.current().getSpanContext().getSpanId());
                return 10086;
            };
            Future<Integer> result = executorService.submit(callable);

            Assertions.assertEquals(Integer.valueOf(10086), result.get());
            executorService.shutdown();
        }
    }
}
