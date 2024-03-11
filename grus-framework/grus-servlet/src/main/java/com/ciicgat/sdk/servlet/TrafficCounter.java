/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author andy
 */
public class TrafficCounter {

    private TrafficCounter() {
        throw new IllegalStateException("Utility class");
    }

    private static final ConcurrentMap<String, AtomicLong> trafficCounterCollector = new ConcurrentHashMap<>();
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public static void trafficIncrement() {
        String key = LocalDateTime.now().format(FORMAT);
        trafficCounterCollector.computeIfAbsent(key, k -> new AtomicLong()).incrementAndGet();
    }

    public static Map<String, Long> getCounter() {
        Map<String, Long> res = new TreeMap<>();
        for (Map.Entry<String, AtomicLong> entry : trafficCounterCollector.entrySet()) {
            res.put(entry.getKey(), entry.getValue().longValue());
        }
        return res;
    }
}
