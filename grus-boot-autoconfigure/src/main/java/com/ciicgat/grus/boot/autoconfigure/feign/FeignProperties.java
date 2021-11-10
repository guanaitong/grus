/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by August.Zhou on 2019-05-29 11:23.
 */
@ConfigurationProperties(prefix = "grus.feign")
public class FeignProperties {

    /**
     * 打印feign请求日志
     */
    private boolean logReq;

    /**
     * 打印feign返回日志
     */
    private boolean logResp;

    /**
     * 启用sentinel
     */
    private boolean enableSentinel;

    public boolean isLogReq() {
        return logReq;
    }

    public void setLogReq(boolean logReq) {
        this.logReq = logReq;
    }

    public boolean isLogResp() {
        return logResp;
    }

    public void setLogResp(boolean logResp) {
        this.logResp = logResp;
    }

    public boolean isEnableSentinel() {
        return enableSentinel;
    }

    public void setEnableSentinel(boolean enableSentinel) {
        this.enableSentinel = enableSentinel;
    }
}
