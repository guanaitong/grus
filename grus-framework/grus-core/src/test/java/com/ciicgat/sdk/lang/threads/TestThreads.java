/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.threads;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ThreadFactory;

/**
 * Created by August.Zhou on 2018-10-22 13:35.
 */
public class TestThreads {

    @Test
    public void testSleep() {
        long start = System.currentTimeMillis();
        Threads.sleepSeconds(2);

        Assert.assertEquals(2, (System.currentTimeMillis() - start) / 1000);

        long start2 = System.currentTimeMillis();
        Threads.sleep(1548);
        Assert.assertTrue(1548 - ((System.currentTimeMillis() - start2)) < 100);

    }


    @Test
    public void testThreadFactory() {
        ThreadFactory threadFactory = Threads.newDaemonThreadFactory("testXXX");


        Thread thread = threadFactory.newThread(() -> System.out.println(System.currentTimeMillis()));


        Assert.assertTrue(thread.getName().startsWith("testXXX"));

        Assert.assertEquals(thread.getPriority(), Thread.NORM_PRIORITY);

        Assert.assertEquals(thread.getUncaughtExceptionHandler(), Threads.LOGGER_UNCAUGHTEXCEPTIONHANDLER);
    }

    @Test
    public void testNewThread() {

        Thread thread = Threads.newThread(() -> System.out.println(System.currentTimeMillis()), "testXXX", Thread.MAX_PRIORITY);


        Assert.assertTrue(thread.getName().startsWith("testXXX"));

        Assert.assertEquals(thread.getPriority(), Thread.MAX_PRIORITY);

        Assert.assertEquals(thread.getUncaughtExceptionHandler(), Threads.LOGGER_UNCAUGHTEXCEPTIONHANDLER);
    }
}
