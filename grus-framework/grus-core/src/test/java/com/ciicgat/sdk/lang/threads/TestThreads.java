/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.threads;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadFactory;

/**
 * Created by August.Zhou on 2018-10-22 13:35.
 */
public class TestThreads {

    @Test
    public void testSleep() {
        long start = System.currentTimeMillis();
        Threads.sleepSeconds(1);

        Assertions.assertEquals(1, (System.currentTimeMillis() - start) / 1000);

        long start2 = System.currentTimeMillis();
        Threads.sleep(348);
        Assertions.assertTrue(348 - ((System.currentTimeMillis() - start2)) < 100);

    }


    @Test
    public void testThreadFactory() {
        ThreadFactory threadFactory = Threads.newDaemonThreadFactory("testXXX");


        Thread thread = threadFactory.newThread(() -> System.out.println(System.currentTimeMillis()));


        Assertions.assertTrue(thread.getName().startsWith("testXXX"));

        Assertions.assertEquals(thread.getPriority(), Thread.NORM_PRIORITY);

        Assertions.assertEquals(thread.getUncaughtExceptionHandler(), Threads.LOGGER_UNCAUGHTEXCEPTIONHANDLER);
    }

    @Test
    public void testNewThread() {

        Thread thread = Threads.newThread(() -> System.out.println(System.currentTimeMillis()), "testXXX", Thread.MAX_PRIORITY);


        Assertions.assertTrue(thread.getName().startsWith("testXXX"));

        Assertions.assertEquals(thread.getPriority(), Thread.MAX_PRIORITY);

        Assertions.assertEquals(thread.getUncaughtExceptionHandler(), Threads.LOGGER_UNCAUGHTEXCEPTIONHANDLER);
    }
}
