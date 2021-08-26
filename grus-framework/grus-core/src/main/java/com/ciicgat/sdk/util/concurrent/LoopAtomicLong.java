/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/27 10:02
 * @Description:
 */
public class LoopAtomicLong extends AtomicLong {

    private long loopMax;

    /**
     * 线程安全的循环递增序列
     *
     * @param maxSequence 返回的序列值 [0, maxSequence)
     */
    public LoopAtomicLong(long maxSequence) {
        super();
        this.loopMax = maxSequence - 1;
    }

    public LoopAtomicLong(long initialValue, long maxSequence) {
        super(initialValue);
        this.loopMax = maxSequence - 1;
    }

    public long loopGet() {
        do {
            long value = get();
            long newValue = (value == loopMax) ? 0 : value + 1;

            if (compareAndSet(value, newValue)) {
                return newValue;
            }
        } while (true);
    }
}
