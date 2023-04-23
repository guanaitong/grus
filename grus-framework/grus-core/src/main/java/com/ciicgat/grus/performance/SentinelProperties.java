/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.sdk.gconf.annotation.BeanFieldKey;

/**
 * Created by August.Zhou on 2022/10/20 17:49
 */
public class SentinelProperties {
    @BeanFieldKey("client.maxconcurrency")
    private int clientMaxConcurrency = 16;
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
}
