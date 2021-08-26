/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.core;

import com.ciicgat.grus.performance.SlowLogger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * Created by August.Zhou on 2019-07-05 10:16.
 */
@ConfigurationProperties(prefix = "grus.core")
public class CoreProperties {

    /**
     * true时，会打印慢日志
     */
    private boolean printSlowLog = true;


    /**
     * true时，会发送慢日志
     */
    private boolean sendSlowLog = true;


    @PostConstruct
    private void init() {
        if (!printSlowLog) {
            SlowLogger.closePrintSlowLog();
        }
        if (!sendSlowLog) {
            SlowLogger.closeSendSlowLog();
        }
    }

    public boolean isPrintSlowLog() {
        return printSlowLog;
    }

    public void setPrintSlowLog(boolean printSlowLog) {
        this.printSlowLog = printSlowLog;
    }

    public boolean isSendSlowLog() {
        return sendSlowLog;
    }

    public void setSendSlowLog(boolean sendSlowLog) {
        this.sendSlowLog = sendSlowLog;
    }
}
