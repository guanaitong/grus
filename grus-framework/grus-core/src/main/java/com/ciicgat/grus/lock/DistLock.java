/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.lock;

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
     * 获取锁之后，会执行runnable。
     *
     * @param lockKey
     * @param runnable
     */
    void execute(String lockKey, Runnable runnable);

    /**
     * 会一直尝试获取锁，直到获取为止。这个过程是阻塞行为。
     * 获取锁之后，会执行supplier。返回的value是supplier的返回值。
     *
     * @param lockKey
     * @param supplier
     * @param <T>
     * @return
     */
    default <T> T execute(String lockKey, Supplier<T> supplier) {
        RunnableSupplier<T> runnableSupplier = new RunnableSupplier<>(supplier);
        execute(lockKey, runnableSupplier);
        return runnableSupplier.value();
    }

    /**
     * 尝试一次机会去获取锁，如果获取成功，会执行runnable，同时返回true。
     * 获取失败，那么不会执行runnable，返回false。
     * 很明显，此方法不阻塞。
     *
     * @param lockKey
     * @param runnable
     * @return
     */
    boolean tryExecuteOnce(String lockKey, Runnable runnable);

    /**
     * 尝试一次机会去获取锁，如果获取成功，会执行supplier.get()，同时返回true和supplier的结果。
     * 获取失败，那么不会执行runnable，返回false。
     * 很明显，此方法不阻塞。
     *
     * @param lockKey
     * @param supplier
     * @return
     */
    default <T> Result<T> tryExecuteOnce(String lockKey, Supplier<T> supplier) {
        RunnableSupplier<T> runnableSupplier = new RunnableSupplier<>(supplier);
        boolean r = tryExecuteOnce(lockKey, runnableSupplier);
        if (r) {
            return new Result<>(true, runnableSupplier.value());
        } else {
            return new Result<>(false, null);
        }
    }

    /**
     * 给定时间内获取锁，如果获取成功，会执行runnable，同时返回true。
     * 获取失败，那么不会执行runnable，返回false。
     *
     * @param lockKey
     * @param runnable
     * @param time
     * @param unit
     * @return
     */
    boolean tryExecute(String lockKey, long time, TimeUnit unit, Runnable runnable);


    /**
     * 给定时间内获取锁，如果获取成功，会执行runnable，同时返回true。
     * 获取失败，那么不会执行runnable，返回false。
     *
     * @param lockKey
     * @param duration
     * @param runnable
     * @return
     */
    default boolean tryExecute(String lockKey, Duration duration, Runnable runnable) {
        return tryExecute(lockKey, duration.toNanos(), TimeUnit.NANOSECONDS, runnable);
    }

    /**
     * 给定时间内获取锁，如果获取成功，会执行supplier.get()，同时返回true和supplier的结果。
     * 获取失败，那么不会执行supplier.get()，返回false。
     *
     * @param lockKey
     * @param duration
     * @param supplier
     * @param <T>
     * @return
     */
    default <T> Result<T> tryExecute(String lockKey, Duration duration, Supplier<T> supplier) {
        RunnableSupplier<T> runnableSupplier = new RunnableSupplier<>(supplier);
        boolean r = tryExecute(lockKey, duration, runnableSupplier);
        if (r) {
            return new Result<>(true, runnableSupplier.value());
        } else {
            return new Result<>(false, null);
        }
    }


}
