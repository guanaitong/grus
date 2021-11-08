/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.lock;

import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by August.Zhou on 2019-08-01 12:51.
 */
public class RedisLockTest {

    @Test
    public void test() {
//        Pool<Jedis> pool = new JedisSentinelPool("mymaster", Set.of("192.168.223.190:26379"));

//        Pool<Jedis> pool = new JedisPool(new JedisPoolConfig(), "redis.servers.dev.ofc", 6379,
//                2000, null,
//                1, "test"
//        );
        RedisLock redisLock = new RedisLock();
//        RedisLock redisLock = new RedisLock(pool);
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String key = sessionIdGenerator.generateSessionId();
        String v = redisLock.tryAcquire(key);
        Assertions.assertNotNull(v);
        Assertions.assertTrue(redisLock.release(key, v));
    }

    @Test
    public void test1() throws InterruptedException {
//        Pool<Jedis> pool = new JedisSentinelPool("mymaster", Set.of("192.168.223.190:26379"));

//        Pool<Jedis> pool = new JedisPool(new JedisPoolConfig(), "redis.servers.dev.ofc", 6379,
//                2000, null,
//                1, "test"
//        );
//        RedisLock redisLock = new RedisLock(pool);
        RedisLock redisLock = new RedisLock();
        LongAdder longAdder = new LongAdder();
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String key = sessionIdGenerator.generateSessionId();
        int threadNum = 7;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100; j++) {
                        String v = redisLock.tryAcquire(key);
                        if (v != null) {
                            longAdder.add(1);
                        }
                    }
                    countDownLatch.countDown();
                }
            }).start();


        }
        countDownLatch.await();
        Assertions.assertTrue(1 == longAdder.longValue());
    }


}
