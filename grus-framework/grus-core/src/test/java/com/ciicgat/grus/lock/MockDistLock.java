/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.lock;

import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2024/4/19 20:34
 */
public class MockDistLock implements DistLock {
    private boolean success;

    public MockDistLock(boolean success) {
        this.success = success;
    }

    @Override
    public void execute(String lockKey, Runnable runnable) {
        runnable.run();
    }

    @Override
    public boolean tryExecuteOnce(String lockKey, Runnable runnable) {
        runnable.run();
        return success;
    }

    @Override
    public boolean tryExecute(String lockKey, long time, TimeUnit unit, Runnable runnable) {
        runnable.run();
        return success;
    }
}
