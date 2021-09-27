/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
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


    public static boolean logEvent(Module module, long duration, String detail) {
        Level level = module.getLevelByDuration(duration);
        boolean isSlow = level.biggerThan(module.getSlowLevel());
        if (isSlow) {
            String msg = String.format(FORMAT, module.getName(), level.name(), duration, detail);
            if (printSlowLog) {
                LOGGER.warn(msg);
            }
            if (sendSlowLog) {
                boolean alertLevel = level.biggerThan(module.getAlertLevel());
                if (alertLevel && isHttpThread()) {
                    Alert.send(msg);
                }
            }
        } else if (LOGGER.isDebugEnabled()) {
            String msg = String.format(FORMAT, module.getName(), level.name(), duration, detail);
            LOGGER.debug(msg);
        }
        return isSlow;
    }

    private static boolean isHttpThread() {
        return Thread.currentThread().getName().startsWith("http-nio");
    }
}
