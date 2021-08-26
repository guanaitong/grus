/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.lock;

import com.ciicgat.grus.lock.DistLock;
import com.ciicgat.grus.zk.TestZkConfig;
import com.ciicgat.grus.zk.ZKUtils;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 11:23
 * @Description:
 */
public class ZKDistLockTest {

    private final int threadNums = 5;
    private final int repeat = 10000;
    private CyclicBarrier barrier = new CyclicBarrier(threadNums);
    private CountDownLatch latch = new CountDownLatch(threadNums);

    private int count = 0;

    private class LockRunner implements Runnable {

        private DistLock lock;

        LockRunner(DistLock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (Exception e) {
                // do nothing
            }

            if (System.currentTimeMillis() % 2 == 0) {
                lock.acquire();
            } else {
                if (!lock.tryAcquire(1, TimeUnit.SECONDS)) {
                    throw new RuntimeException("锁获取失败");
                }
            }

            for (int i = 0; i < repeat; i++) {
                count++;
            }

            lock.release();
            latch.countDown();
        }
    }

    @Test
    public void testLock() throws Exception {
        CuratorFramework curator = ZKUtils.init(TestZkConfig.ZK);
        ZKDistLockFactory factory = new ZKDistLockFactory(curator, "payment");
        DistLock distLock = factory.buildLock("for-test");

        for (int i = 0; i < threadNums; i++) {
            LockRunner lockRunner = new LockRunner(distLock);
            Thread thread = new Thread(lockRunner);
            thread.start();
        }

        latch.await();
        Assert.assertEquals(threadNums * repeat, count);
    }
}
