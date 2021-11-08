/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import com.ciicgat.sdk.util.system.Systems;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.RateLimitingSampler;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.junit.jupiter.api.Assertions;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

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

    @BeforeClass
    public static void registerJaegerTracer() {
        Configuration.SenderConfiguration senderConfiguration =
                new Configuration
                        .SenderConfiguration()
                        .withAgentHost("127.0.0.1")
                        .withAgentPort(6831);

        Configuration.ReporterConfiguration reporterConfig =
                new Configuration
                        .ReporterConfiguration()
                        .withSender(senderConfiguration)
                        .withLogSpans(false);

        Float parm = "unknown".equals(Systems.APP_NAME) ? 0f : 50f;
        //采样配置
        Configuration.SamplerConfiguration samplerConfig =
                new Configuration
                        .SamplerConfiguration()
                        .withType(RateLimitingSampler.TYPE)
                        .withParam(parm);

        JaegerTracer tracer = new Configuration(Systems.APP_NAME).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
        GlobalTracer.register(tracer);
    }


    @Test
    public void test() throws ExecutionException, InterruptedException {

        testRunnable(new TraceThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>()));
        testRunnable(TraceThreadPoolExecutor.wrap(Executors.newSingleThreadExecutor()));

        testCallable(new TraceThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>()));
        testCallable(TraceThreadPoolExecutor.wrap(Executors.newCachedThreadPool()));

    }

    public void testRunnable(ExecutorService executorService) throws InterruptedException {
        Tracer tracer = GlobalTracer.get();
        final Span span = tracer.buildSpan("main")
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                .start();
        Spans.setRootSpan(span);

        String spanId = MDC.get("spanId");
        Assertions.assertNotNull(spanId);
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Assertions.assertEquals(spanId, MDC.get("spanId"));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        executorService.shutdown();
    }


    public void testCallable(ExecutorService executorService) throws ExecutionException, InterruptedException {
        Tracer tracer = GlobalTracer.get();
        final Span span = tracer.buildSpan("main")
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                .start();
        Spans.setRootSpan(span);

        String spanId = MDC.get("spanId");
        Assertions.assertNotNull(spanId);

        Callable<Integer> callable = () -> {
            Assertions.assertEquals(spanId, MDC.get("spanId"));
            return 10086;
        };
        Future<Integer> result = executorService.submit(callable);

        Assertions.assertEquals(Integer.valueOf(10086), result.get());
        executorService.shutdown();
    }
}
