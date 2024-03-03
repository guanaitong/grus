/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.gconf.annotation.BeanFieldKey;

/**
 * Created by August.Zhou on 2022/10/20 17:49
 */
public class SentinelDefaultConfig {
    @BeanFieldKey("client.maxconcurrency")
    private int clientMaxConcurrency = 16;

    @BeanFieldKey("client.slowRatioThreshold")
    private double clientSlowRatioThreshold = 0.5;

    @BeanFieldKey("client.slowThresholdMs")
    private int clientSlowThresholdMs = 2000;

    @BeanFieldKey("client.minRequestAmount")
    private int clientMinRequestAmount = 5;


    @BeanFieldKey("web.maxconcurrency")
    private int webMaxConcurrency = 16;

    public int getClientMaxConcurrency() {
        return clientMaxConcurrency;
    }

    public void setClientMaxConcurrency(int clientMaxConcurrency) {
        this.clientMaxConcurrency = clientMaxConcurrency;
    }

    public int getWebMaxConcurrency() {
        return webMaxConcurrency;
    }

    public void setWebMaxConcurrency(int webMaxConcurrency) {
        this.webMaxConcurrency = webMaxConcurrency;
    }

    public double getClientSlowRatioThreshold() {
        return clientSlowRatioThreshold;
    }

    public void setClientSlowRatioThreshold(double clientSlowRatioThreshold) {
        this.clientSlowRatioThreshold = clientSlowRatioThreshold;
    }

    public int getClientSlowThresholdMs() {
        return clientSlowThresholdMs;
    }

    public void setClientSlowThresholdMs(int clientSlowThresholdMs) {
        this.clientSlowThresholdMs = clientSlowThresholdMs;
    }

    public int getClientMinRequestAmount() {
        return clientMinRequestAmount;
    }

    public void setClientMinRequestAmount(int clientMinRequestAmount) {
        this.clientMinRequestAmount = clientMinRequestAmount;
    }

    public static SentinelDefaultConfig getInstance() {
        return GlobalGconfConfig.getConfig().getBean("sentinel.properties",
                SentinelDefaultConfig.class);
    }
}
