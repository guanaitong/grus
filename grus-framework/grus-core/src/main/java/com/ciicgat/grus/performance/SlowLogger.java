/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.LatencyConfig;
import com.ciicgat.grus.core.LatencyLevel;
import com.ciicgat.grus.core.Module;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by August.Zhou on 2019-06-26 11:19.
 */
public class SlowLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlowLogger.class);

    private static boolean printSlowLog = true;

    private static boolean sendSlowLog = true;

    private static final String FORMAT = "%s slow,level [%s],cost [%d] millis,detail-> %s";

    private SlowLogger() {
    }

    public static void closePrintSlowLog() {
        SlowLogger.printSlowLog = false;
    }

    public static void setPrintSlowLog(boolean printSlowLog) {
        SlowLogger.printSlowLog = printSlowLog;
    }

    public static void closeSendSlowLog() {
        SlowLogger.sendSlowLog = false;
    }

    public static void setSendSlowLog(boolean sendSlowLog) {
        SlowLogger.sendSlowLog = sendSlowLog;
    }

    public static boolean logEvent(Module module, Span span, String detail) {
        if (span instanceof ReadWriteSpan readWriteSpan) {
            return SlowLogger.logEvent(module, readWriteSpan.getLatencyNanos(), detail);
        }
        return false;
    }

    public static boolean logEvent(Module module, long nanosDuration, String detail) {
        LatencyConfig latencyConfig = LatencyConfig.getModuleConfig(module);
        LatencyLevel latencyLevel = latencyConfig.getLevel(nanosDuration);
        if (latencyLevel == LatencyLevel.SLOW) {
            String msg = String.format(FORMAT, module.getName(), latencyLevel.name(), nanosDuration / 1000_1000L, detail);
            if (printSlowLog) {
                LOGGER.warn(msg);
            }
            if (sendSlowLog && isHttpThread()) {
                Alert.send(msg);
            }
            return true;
        } else if (latencyLevel == LatencyLevel.MEDIUM) {
            String msg = String.format(FORMAT, module.getName(), latencyLevel.name(), nanosDuration / 1000_1000L, detail);
            LOGGER.warn(msg);
        }
        return false;
    }

    private static boolean isHttpThread() {
        return Thread.currentThread().getName().startsWith("http-nio");
    }
}
