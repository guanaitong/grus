/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/27 10:12
 * @Description:
 */
public class LoopAtomicLongTest {

    private final int threadNums = 320;
    private final int maxSequence = 10;

    private ArrayBlockingQueue<Long> blockingQueue = new ArrayBlockingQueue<>(threadNums * maxSequence * 10);
    private CyclicBarrier barrier = new CyclicBarrier(threadNums);
    private CountDownLatch latch = new CountDownLatch(threadNums);
    private LoopAtomicLong loopAtomicLong = new LoopAtomicLong(maxSequence);

    private class LoopRunner implements Runnable {

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (Exception e) {
                // do nothing
            }

            for (int i = 0; i < maxSequence * 10; i++) {
                blockingQueue.add(loopAtomicLong.loopGet());
            }
            latch.countDown();
        }
    }

    @Test
    public void testLoop() throws Exception {
        for (int i = 0; i < threadNums; i++) {
            LoopRunner loopRunner = new LoopRunner();
            Thread thread = new Thread(loopRunner);
            thread.start();
        }

        latch.await();

        Iterator<Long> iterator = blockingQueue.iterator();
        int[] times = new int[maxSequence];
        while (iterator.hasNext()) {
            times[(iterator.next()).intValue()]++;
        }

        for (int i = 0; i < maxSequence; i++) {
            Assertions.assertEquals(threadNums * maxSequence, times[i]);
        }
    }
}
