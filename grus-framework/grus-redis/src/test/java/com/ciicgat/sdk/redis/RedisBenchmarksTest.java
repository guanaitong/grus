/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.sdk.redis.config.RedisSetting;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2018/9/19 10:30.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class RedisBenchmarksTest {

    private static RedisExecutor redisExecutor;

    static {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().build();
        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance());

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));
        GlobalOpenTelemetry.resetForTest();
        OpenTelemetrys.set(sdk);
        RedisSetting redisSetting = new RedisSetting();
        redisSetting.setDb(0);
        redisSetting.setType(0);
        RedisSetting.StandaloneConfig standaloneConfig = new RedisSetting.StandaloneConfig();
        standaloneConfig.setHost("10.101.11.46");
        standaloneConfig.setPort(6379);
        redisSetting.setStandalone(standaloneConfig);
        redisExecutor = redisSetting.newRedisExecutor();
        String key = String.valueOf(1);
        final String value = Math.random() + "";
        redisExecutor.execute(jedis -> jedis.set(key, value));
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RedisBenchmarksTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(100)
                .warmupTime(TimeValue.milliseconds(1))
                .measurementTime(TimeValue.milliseconds(1))
                .measurementIterations(500)
                .build();

        new Runner(opt).run();
    }

    @Benchmark//对要被测试性能的代码添加注解，说明该方法是要被测试性能的
    @Threads(100)
    public void set() {
        String key = String.valueOf(1);
        final String value = Math.random() + "";
        redisExecutor.execute(jedis -> jedis.set(key, value));
    }


}
