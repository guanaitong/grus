/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficCounter {
    private static final ConcurrentMap<String, AtomicLong> trafficCounterCollector = new ConcurrentHashMap<>();
    private final static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public static void TrafficIncrement() {
        String key = LocalDateTime.now().format(FORMAT);
        trafficCounterCollector.computeIfAbsent(key, k -> new AtomicLong()).incrementAndGet();
    }

    public static ConcurrentMap<String, AtomicLong> GetCounter() {
        return trafficCounterCollector;
    }
}
