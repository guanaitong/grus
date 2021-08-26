/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2018/8/28 14:25.
 */
public class TestRollingNumber {

    @Test
    public void test() throws InterruptedException {
        final RollingNumber qpsCalculator = new RollingNumber();
        int threadNum = 4;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < threadNum; i++) {
            threadList.add(new Thread() {
                public void run() {
                    for (int i = 0; i < 5000; i++) {
                        qpsCalculator.record();
                    }
                    countDownLatch.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();
        for (Thread thread : threadList) {
            thread.start();
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("totalMilliseconds:  " + totalTime);

        for (int i = 0; i < 5000; i++) {
            qpsCalculator.record();
        }

        long endTime1 = System.currentTimeMillis();
        long totalTime1 = endTime1 - endTime;
        System.out.println("totalMilliseconds:  " + totalTime1);
        TimeUnit.SECONDS.sleep(20);

    }
}
