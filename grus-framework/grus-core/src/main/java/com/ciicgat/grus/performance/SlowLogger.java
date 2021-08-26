/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.util.frigate.FrigateMessage;
import com.ciicgat.sdk.util.frigate.FrigateRawNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import com.ciicgat.sdk.util.system.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
                    FrigateMessage frigateMessage = FrigateMessage.newInstance();
                    frigateMessage.setChannel(NotifyChannel.QY_WE_CHAT.code());
                    frigateMessage.setContent(msg);
                    frigateMessage.setModule(module.getName());
                    frigateMessage.setTitle(module.getName() + " slow");
                    frigateMessage.setTags(Map.of("duration", String.valueOf(duration), "level", level.name()));
                    FrigateRawNotifier.sendMsgByAppNames(List.of(Systems.APP_NAME), frigateMessage);
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
