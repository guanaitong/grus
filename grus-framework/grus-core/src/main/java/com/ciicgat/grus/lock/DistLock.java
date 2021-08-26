/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 10:11
 * @Description: 分布式锁接口
 */
public interface DistLock {
    void acquire();

    boolean tryAcquire(long time, TimeUnit unit);

    void release();
}
