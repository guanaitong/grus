/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

/**
 * Created by August on 2021/8/31
 */
class MethodPrintConfig {
    private final boolean log;
    private final int[] requestLogParameterIndex;
    private final boolean logReq;
    private final boolean logResp;

    MethodPrintConfig() {
        this(new int[0], false, false);
    }

    MethodPrintConfig(int[] requestLogParameterIndex, boolean logReq, boolean logResp) {
        this.log = logReq || logReq;
        this.requestLogParameterIndex = requestLogParameterIndex;
        this.logReq = logReq;
        this.logResp = logResp;
    }

    public boolean isLog() {
        return log;
    }

    public int[] getRequestLogParameterIndex() {
        return requestLogParameterIndex;
    }

    public boolean isLogReq() {
        return logReq;
    }

    public boolean isLogResp() {
        return logResp;
    }
}
