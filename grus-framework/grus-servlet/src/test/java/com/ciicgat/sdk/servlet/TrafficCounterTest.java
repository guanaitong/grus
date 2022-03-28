/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.ciicgat.sdk.servlet.TrafficCounter.TrafficIncrement;

public class TrafficCounterTest {
    @Test
    public void test() {
        TrafficIncrement();
        TrafficIncrement();
        TrafficIncrement();
        ConcurrentMap<String, AtomicLong> counterMap = TrafficCounter.GetCounter();
        Assertions.assertTrue(counterMap.size() > 0);
    }
}
