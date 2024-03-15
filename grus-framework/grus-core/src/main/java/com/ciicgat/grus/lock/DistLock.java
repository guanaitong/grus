/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.lock;

import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 10:11
 * @Description: 分布式锁接口
 */
public interface DistLock {
    /**
     * 会一直尝试获取锁，直到获取为止。这个过程是阻塞行为。
     *
     * @param lockKey
     * @param runnable
     */
    void execute(String lockKey, Runnable runnable);

    default <T> T execute(String lockKey, Supplier<T> supplier) {
        RunnableSupplier<T> runnableSupplier = new RunnableSupplier<T>(supplier);
        execute(lockKey, runnableSupplier);
        return runnableSupplier.value();
    }

    /**
     * 给定时间内获取锁，如果获取成功，会执行runnable。如果执行了runnable，那么会返回true
     *
     * @param lockKey
     * @param runnable
     * @param time
     * @param unit
     * @return
     */
    boolean tryExecute(String lockKey, long time, TimeUnit unit, Runnable runnable);


    default <T> Pair<Boolean, T> tryExecute(String lockKey, Duration duration, Supplier<T> supplier) {
        RunnableSupplier<T> runnableSupplier = new RunnableSupplier<T>(supplier);
        boolean r = tryExecute(lockKey, duration, runnableSupplier);
        if (r) {
            return Pair.of(true, runnableSupplier.value());
        } else {
            return Pair.of(false, null);
        }
    }

    default boolean tryExecute(String lockKey, Duration duration, Runnable runnable) {
        return tryExecute(lockKey, duration.toNanos(), TimeUnit.NANOSECONDS, runnable);
    }
}
