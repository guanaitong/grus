/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * shut down hook for closing  thread
 * Created by August.zhou on 2017/7/28 13:34.
 */
public class ShutdownHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);

    /**
     * addShutdownHook
     *
     * @param runnable instance of runnable
     */
    public static void addShutdownHook(Runnable runnable) {
        if (runnable instanceof Thread) {
            throw new IllegalArgumentException();
        }
        boolean offer = TasksHolder.TASKS_BEFORE_SHUTDOWN.offer(runnable);
        LOGGER.info("addShutdownHook status {}", offer);
    }

    private static class TasksHolder {
        private static final BlockingQueue<Runnable> TASKS_BEFORE_SHUTDOWN = new ArrayBlockingQueue<>(200);

        static {
            Runtime.getRuntime().addShutdownHook(Threads.newThread(() -> TASKS_BEFORE_SHUTDOWN.forEach(runnable -> {
                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    LOGGER.error("shutdownHook_error", throwable);
                }
            }), "shutHook", Thread.MIN_PRIORITY));
        }

    }
}
