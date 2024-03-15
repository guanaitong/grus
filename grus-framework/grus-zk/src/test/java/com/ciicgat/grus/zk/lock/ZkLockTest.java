/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.lock;

import com.ciicgat.grus.zk.TestZkConfig;
import com.ciicgat.grus.zk.ZKUtils;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by August.Zhou on 2019-08-01 12:51.
 */
public class ZkLockTest {
    @Test
    public void test() throws InterruptedException {
        CuratorFramework curator = ZKUtils.init(TestZkConfig.ZK);
        ZKDistLock redisLock = new ZKDistLock(curator);
        LongAdder longAdder = new LongAdder();
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String key = sessionIdGenerator.generateSessionId();
        int threadNum = 7;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        int loopCount = 10;
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
        ZKDistLock zkLock1 = new ZKDistLock(ZKUtils.init(TestZkConfig.ZK));
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String key = sessionIdGenerator.generateSessionId();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                zkLock1.execute(key, new Runnable() {
                    @Override
                    public void run() {
                        countDownLatch.countDown();
                        Threads.sleepSeconds(30);
                        System.out.println(1);
                    }
                });
            }
        }).start();

        countDownLatch.await();
        boolean r1 = zkLock1.tryExecute(key, 3, TimeUnit.SECONDS, new Runnable() {
            @Override
            public void run() {

                System.out.println(1);
            }
        });
        Assertions.assertEquals(false, r1);

        ZKDistLock redislock2 = new ZKDistLock(ZKUtils.init(TestZkConfig.ZK));
        boolean r2 = redislock2.tryExecute(key, 3, TimeUnit.SECONDS, new Runnable() {
            @Override
            public void run() {

                System.out.println(1);
            }
        });
        Assertions.assertEquals(false, r2);
    }

}
