/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.lock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

/**
 * Created by August.Zhou on 2024/4/19 20:25
 */
class DistLockTest {

    @Test
    void execute() {
        DistLock distLock = new MockDistLock(true);
        String key = "123";
        Integer r = distLock.execute(key, () -> 1);
        Assertions.assertEquals(1, r);
    }

    @Test
    void tryExecuteOnce() {
        DistLock distLock = new MockDistLock(true);
        String key = "123";
        Result<Integer> r = distLock.tryExecuteOnce(key, () -> 1);
        Assertions.assertTrue(r.isSuccess());
        Assertions.assertEquals(1, r.getValue());
    }

    @Test
    void tryExecuteOnce1() {
        DistLock distLock = new MockDistLock(false);
        String key = "123";
        Result<Integer> r = distLock.tryExecuteOnce(key, () -> 1);
        Assertions.assertFalse(r.isSuccess());
        Assertions.assertNull(r.getValue());
    }

    @Test
    void tryExecute() {
        DistLock distLock = new MockDistLock(true);
        String key = "123";
        Result<Integer> r = distLock.tryExecute(key, Duration.ofSeconds(1), () -> 1);
        Assertions.assertTrue(r.isSuccess());
        Assertions.assertEquals(1, r.getValue());
    }

    @Test
    void tryExecute1() {
        DistLock distLock = new MockDistLock(false);
        String key = "123";
        Result<Integer> r = distLock.tryExecute(key, Duration.ofSeconds(1), () -> 1);
        Assertions.assertFalse(r.isSuccess());
        Assertions.assertNull(r.getValue());
    }
}
