/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lock;

import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import com.ciicgat.sdk.redis.config.RedisSetting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by August.Zhou on 2019-08-01 12:51.
 */
public class RedisLockTest {
    @Test
    public void test() throws InterruptedException {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("for-test-java");
        RedisSetting redisSetting = configCollection.getBean("redis-config.json", RedisSetting.class);
        RedisLock redisLock = new RedisLock(redisSetting.newRedisExecutor());
        LongAdder longAdder = new LongAdder();
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String key = sessionIdGenerator.generateSessionId();
        int threadNum = 7;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        int loopCount = 100;
        for (int i = 0; i < threadNum; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < loopCount; j++) {
                        redisLock.execute(key, new Runnable() {
                            @Override
                            public void run() {
                                longAdder.add(1);
                            }
                        });
                    }
                    countDownLatch.countDown();
                }
            }).start();


        }
        countDownLatch.await();
        Assertions.assertEquals(loopCount * threadNum, longAdder.longValue());
    }

    @Test
    public void testTimeout() throws InterruptedException {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("for-test-java");
        RedisSetting redisSetting = configCollection.getBean("redis-config.json", RedisSetting.class);
        RedisLock redisLock = new RedisLock(redisSetting.newRedisExecutor());
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String key = sessionIdGenerator.generateSessionId();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                redisLock.execute(key, new Runnable() {
                    @Override
                    public void run() {
                        countDownLatch.countDown();
                        Threads.sleepSeconds(30);
                    }
                });
            }
        }).start();

        countDownLatch.await();

        boolean r = redisLock.tryExecute(key, 3, TimeUnit.SECONDS, () -> {});
        Assertions.assertFalse(r);
    }

}
