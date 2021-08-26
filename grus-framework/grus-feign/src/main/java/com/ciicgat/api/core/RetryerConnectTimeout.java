/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import feign.RetryableException;
import feign.Retryer;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @Author: August
 * @Date: 2021/7/8 15:32
 */
public class RetryerConnectTimeout extends Retryer.Default {
    public RetryerConnectTimeout() {
        super(100, SECONDS.toMillis(1), 3);
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (e.getCause() instanceof java.net.SocketTimeoutException && e.getCause().getMessage().contains("connect timed out")) {
            super.continueOrPropagate(e);
        } else {
            throw e;
        }
    }

    @Override
    public Retryer clone() {
        return new RetryerConnectTimeout();
    }
}
