/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.core;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Created by August.Zhou on 2021/12/20 13:19.
 */
public class LatencyConfig {
    private long maxFastDurationNanos;
    private long minSlowDurationNanos;

    public LatencyConfig(long maxFastDurationNanos, long minSlowDurationNanos) {
        this.maxFastDurationNanos = maxFastDurationNanos;
        this.minSlowDurationNanos = minSlowDurationNanos;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatencyConfig that = (LatencyConfig) o;
        return maxFastDurationNanos == that.maxFastDurationNanos && minSlowDurationNanos == that.minSlowDurationNanos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxFastDurationNanos, minSlowDurationNanos);
    }

    private static final LatencyConfig DEFAULT = new LatencyConfig(200_000_000L, 2000_000_000L);

    public static LatencyConfig getModuleConfig(Module module) {
        Map<String, LatencyConfig> configMap = GlobalGconfConfig.getConfig().getBean("latency-config.json", s -> {
            try {
                return JSON.parse(s, new TypeReference<>() {
                });
            } catch (Exception e) {
                return Collections.emptyMap();
            }
        });
        return configMap == null ? DEFAULT : configMap.getOrDefault(module.getName(), DEFAULT);
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
