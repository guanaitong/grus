/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.LatencyConfig;
import com.ciicgat.grus.core.LatencyLevel;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.json.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2023/11/30 11:28
 */
public class SlowLogger<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlowLogger.class);
    private final Module module;

    private final LatencyConfig latencyConfig;

    private final boolean sendSlowLog;

    private final SlowLoggerFormatter<T> slowLoggerFormatter;

    private final Cache<Integer, AtomicInteger> metricsCache = Caffeine.newBuilder().scheduler(Scheduler.systemScheduler()).expireAfterWrite(1, TimeUnit.MINUTES).build();

    public SlowLogger(Module module, boolean sendSlowLog, SlowLoggerFormatter<T> slowLoggerFormatter) {
        this.module = Objects.requireNonNull(module);
        this.sendSlowLog = sendSlowLog;
        this.slowLoggerFormatter = Objects.requireNonNull(slowLoggerFormatter);
        this.latencyConfig = LatencyConfig.getModuleConfig(module);
    }

    public void mayLog(Span span, Map<String, Object> tags, T value) {
        if (span instanceof ReadWriteSpan) {
            var readWriteSpan = (ReadWriteSpan) span;
            mayLog(readWriteSpan.getLatencyNanos(), tags, value);
        }
    }

    public void mayLog(long nanosDuration, Map<String, Object> tags, T value) {
        LatencyLevel latencyLevel = latencyConfig.getLevel(nanosDuration);
        if (latencyLevel == LatencyLevel.FAST) {
            return;
        }
        String format = slowLoggerFormatter.format(value);
        SlowLog slowLog = new SlowLog();
        slowLog.setTags(tags);
        slowLog.setCost(nanosDuration);
        slowLog.setModule(module.getName());
        slowLog.setLevel(latencyLevel);
        slowLog.setTs(System.currentTimeMillis());
        slowLog.setDetail(format);

        if (latencyLevel == LatencyLevel.SLOW) {
            int t = metricsCache.get(tags.hashCode(), integer -> new AtomicInteger()).incrementAndGet();
            slowLog.setCountLastMinute(t);
            String log = JSON.toJSONString(slowLog);
            LOGGER.warn(log);
            if (sendSlowLog && t > latencyConfig.getSendSlowLogThreshold()) {
                Alert.send(log);
            }
        } else {
            LOGGER.warn(JSON.toJSONString(slowLog));
        }
    }

}
