/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2016/6/20 16:56.
 */
public class Threads {

    private Threads() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Threads.class);
    private static final ConcurrentMap<String, AtomicInteger> THREAD_NUM_MAP = new ConcurrentHashMap<>();
    public static final Thread.UncaughtExceptionHandler LOGGER_UNCAUGHTEXCEPTIONHANDLER = (t, e) -> LOGGER.error(t.getName(), e);
    public static final RejectedExecutionHandler LOGGER_REJECTEDEXECUTIONHANDLER =
            (r, executor) -> LOGGER.error("rejected,queue size {},task count {},active count {}", executor.getQueue().size(), executor.getTaskCount(), executor.getActiveCount());


    /**
     * 创建一个守护线程工厂
     *
     * @param name 线程名
     * @return
     */
    public static ThreadFactory newDaemonThreadFactory(final String name) {
        return newDaemonThreadFactory(name, Thread.NORM_PRIORITY);
    }

    /**
     * 创建一个守护线程工厂
     *
     * @param name     线程名
     * @param priority 优先级
     * @return
     */
    public static ThreadFactory newDaemonThreadFactory(final String name, final int priority) {
        return r -> newDaemonThread(r, name, priority);
    }

    public static ThreadFactory newThreadFactory(final String name, final int priority, boolean daemon) {
        return r -> newThread(r, name, priority, daemon);
    }

    /**
     * 创建一个守护线程
     *
     * @param runnable
     * @param name
     * @param priority
     * @return
     */
    public static Thread newDaemonThread(Runnable runnable, String name, int priority) {
        return newThread(runnable, name, priority, true);
    }

    /**
     * 创建一个线程
     *
     * @param runnable
     * @param name
     * @param priority
     * @return
     */
    public static Thread newThread(Runnable runnable, String name, int priority) {
        return newThread(runnable, name, priority, false);
    }

    public static Thread newThread(Runnable runnable, String name, int priority, boolean daemon) {
        AtomicInteger atomicInteger = THREAD_NUM_MAP.computeIfAbsent(name, s -> new AtomicInteger());
        Thread thread = new Thread(runnable, name + "_" + atomicInteger.getAndIncrement());
        thread.setPriority(priority);
        thread.setDaemon(daemon);
        thread.setUncaughtExceptionHandler(LOGGER_UNCAUGHTEXCEPTIONHANDLER);
        return thread;
    }


    /**
     * sleep without exception
     *
     * @param milliseconds
     */
    public static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            // NOT CALL Thread.currentThread().interrupt()
            // 调用interrupt中断线程某些时候非本意，线程不会真正中断，比如http线程是不会中断的。但是其中断状态变为true。
            // 此时，就会影响某些api（如redis lettuce），因为它们检测了线程是否中断。
            // 此时，灾难发生了，而且不会恢复。因为无法将http线程的状态重置为正常了。
            LOGGER.error("unexpected", e);
        }
    }

    public static void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) { //NOSONAR
            // DONOT CALL Thread.currentThread().interrupt()
            LOGGER.error("unexpected", e);
        }
    }

}
