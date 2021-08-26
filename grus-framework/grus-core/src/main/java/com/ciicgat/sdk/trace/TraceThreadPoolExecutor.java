/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2019-01-08 11:53.
 */
public class TraceThreadPoolExecutor extends ThreadPoolExecutor {

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        if (command instanceof TraceRunnable) {
            super.execute(command);
        } else {
            super.execute(new TraceRunnable(command));
        }
    }

    public static Executor wrap(final Executor executor) {
        if (executor instanceof TraceThreadPoolExecutor) {
            return executor;
        }
        return command -> {
            if (command instanceof TraceRunnable) {
                executor.execute(command);
            } else {
                executor.execute(new TraceRunnable(command));
            }
        };
    }

    /**
     * 对于已有的ExecutorService对象，我们可以包装下，返回一个带trace的executorService
     *
     * @param executorService
     * @return
     */
    public static ExecutorService wrap(final ExecutorService executorService) {
        if (executorService instanceof TraceThreadPoolExecutor) {
            return executorService;
        }
        return new AbstractExecutorService() {

            @Override
            public void execute(Runnable command) {
                if (command instanceof TraceRunnable) {
                    executorService.execute(command);
                } else {
                    executorService.execute(new TraceRunnable(command));
                }
            }

            @Override
            public void shutdown() {
                executorService.shutdown();
            }

            @Override
            public List<Runnable> shutdownNow() {
                return executorService.shutdownNow();
            }

            @Override
            public boolean isShutdown() {
                return executorService.isShutdown();
            }

            @Override
            public boolean isTerminated() {
                return executorService.isTerminated();
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return executorService.awaitTermination(timeout, unit);
            }

        };
    }
}
