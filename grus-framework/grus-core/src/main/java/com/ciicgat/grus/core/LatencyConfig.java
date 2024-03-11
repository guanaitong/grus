/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.core;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.Map;

/**
 * Created by August.Zhou on 2021/12/20 13:19.
 */
public class LatencyConfig {
    private long maxFastDurationNanos;
    private long minSlowDurationNanos;

    private int sendSlowLogThreshold;

    public LatencyConfig(long maxFastDurationNanos, long minSlowDurationNanos, int sendSlowLogThreshold) {
        this.maxFastDurationNanos = maxFastDurationNanos;
        this.minSlowDurationNanos = minSlowDurationNanos;
        this.sendSlowLogThreshold = sendSlowLogThreshold;
    }

    public LatencyConfig() {
    }

    public void setMaxFastDurationNanos(long maxFastDurationNanos) {
        this.maxFastDurationNanos = maxFastDurationNanos;
    }

    public void setMinSlowDurationNanos(long minSlowDurationNanos) {
        this.minSlowDurationNanos = minSlowDurationNanos;
    }

    public long getMaxFastDurationNanos() {
        return maxFastDurationNanos;
    }

    public long getMinSlowDurationNanos() {
        return minSlowDurationNanos;
    }

    public int getSendSlowLogThreshold() {
        return sendSlowLogThreshold;
    }

    public void setSendSlowLogThreshold(int sendSlowLogThreshold) {
        this.sendSlowLogThreshold = sendSlowLogThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatencyConfig that = (LatencyConfig) o;

        if (maxFastDurationNanos != that.maxFastDurationNanos) return false;
        if (minSlowDurationNanos != that.minSlowDurationNanos) return false;
        return sendSlowLogThreshold == that.sendSlowLogThreshold;
    }

    @Override
    public int hashCode() {
        int result = (int) (maxFastDurationNanos ^ (maxFastDurationNanos >>> 32));
        result = 31 * result + (int) (minSlowDurationNanos ^ (minSlowDurationNanos >>> 32));
        result = 31 * result + sendSlowLogThreshold;
        return result;
    }

    private static final LatencyConfig DEFAULT = new LatencyConfig(200_000_000L, 2000_000_000L, 5);

    public static LatencyConfig getModuleConfig(String module) {
        Map<String, LatencyConfig> configMap = GlobalGconfConfig.getConfig().getBean("latency-config.json", s -> {
            try {
                return JSON.parse(s, new TypeReference<>() {
                });
            } catch (Exception e) {
                return Collections.emptyMap();
            }
        });
        return configMap == null ? DEFAULT : configMap.getOrDefault(module, DEFAULT);
    }

    public LatencyLevel getLevel(final long nanosDuration) {
        if (nanosDuration < maxFastDurationNanos) {
            return LatencyLevel.FAST;
        } else if (nanosDuration < minSlowDurationNanos) {
            return LatencyLevel.MEDIUM;
        }
        return LatencyLevel.SLOW;
    }
}
