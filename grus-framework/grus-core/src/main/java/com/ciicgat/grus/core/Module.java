/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.core;

import com.ciicgat.grus.performance.Level;

import static com.ciicgat.grus.performance.Level.L1;
import static com.ciicgat.grus.performance.Level.L2;
import static com.ciicgat.grus.performance.Level.L3;
import static com.ciicgat.grus.performance.Level.L4;
import static com.ciicgat.grus.performance.Level.L5;
import static com.ciicgat.grus.performance.Level.L6;
import static com.ciicgat.grus.performance.Level.L7;
import static com.ciicgat.grus.performance.Level.L8;
import static com.ciicgat.grus.performance.Level.L9;
import static com.ciicgat.grus.performance.Level.LH;

/**
 * Created by August.Zhou on 2019-06-26 12:37.
 */
public enum Module {

    REDIS("redis",
            new long[]{10, 50, 100, 500, 1000, 2000},
            new Level[]{L1, L2, L3, L4, L5, L6},
            L3,
            L5),
    SERVLET("web-servlet",
            new long[]{200, 500, 1000, 2000, 3000, 4000, 5000},
            new Level[]{L1, L2, L3, L4, L5, L6, L7},
            L2,
            L7),
    HTTP_CLIENT("httpclient",
            new long[]{50, 100, 300, 500, 1000, 2000, 3000},
            new Level[]{L1, L2, L3, L4, L5, L6, L7},
            L3,
            L6),
    FEIGN("feign",
            new long[]{50, 100, 300, 500, 1000, 2000, 3000},
            new Level[]{L1, L2, L3, L4, L5, L6, L7},
            L3,
            L6),
    DB("db",
            new long[]{50, 100, 300, 500, 1000, 2000, 3000, 5000, 10000},
            new Level[]{L1, L2, L3, L4, L5, L6, L7, L8, L9},
            L4,
            L6),
    RABBITMQ("rabbitmq",
            new long[]{50, 100, 300, 500, 1000, 2000, 3000, 5000, 10000},
            new Level[]{L1, L2, L3, L4, L5, L6, L7, L8, L9},
            L4,
            L6);

    private final String name;

    private final long[] intervals;
    private final Level[] levels;
    private final int length;
    private final Level slowLevel;
    private final Level alertLevel;

    Module(String name, long[] intervals, Level[] levels, Level slowLevel, Level alertLevel) {
        this.name = name;
        this.intervals = intervals;
        this.levels = levels;
        if (intervals.length != levels.length) {
            throw new IllegalArgumentException("intervals和levels的长度必须一致");
        }
        this.length = levels.length;
        this.slowLevel = slowLevel;
        this.alertLevel = alertLevel;
    }

    public String getName() {
        return name;
    }

    /**
     * 将消耗时间，转化为level
     *
     * @param durationMillis
     * @return
     */
    public Level getLevelByDuration(long durationMillis) {
        for (int i = 0; i < length; i++) {
            if (durationMillis <= intervals[i]) {
                return levels[i];
            }
        }
        return LH;
    }


    /**
     * 获取为慢处理的level
     *
     * @return
     */
    public Level getSlowLevel() {
        return slowLevel;
    }

    /**
     * 获取需要报警的level
     *
     * @return
     */
    public Level getAlertLevel() {
        return alertLevel;
    }
}
