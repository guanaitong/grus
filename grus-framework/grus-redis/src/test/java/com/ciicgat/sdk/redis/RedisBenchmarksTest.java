/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.redis.config.RedisSetting;
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
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("for-test-java");
        RedisSetting redisSetting = configCollection.getBean("redis-config.json", RedisSetting.class);
        redisExecutor = redisSetting.newRedisExecutor();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RedisBenchmarksTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(30)
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
