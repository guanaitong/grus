/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

import java.util.List;

/**
 * @Author: August
 * @Date: 2021/7/23 16:12
 */
public class RetryCondition {
    static final List<RetryCondition> DEFAULTS;

    static {
        // target ip cannot access
        // remote server shutdown
        DEFAULTS = List.of(new RetryCondition(java.net.SocketTimeoutException.class, "connect timed out"),
                new RetryCondition(java.net.ConnectException.class, "Failed to connect to"));
    }

    private String className;
    private String message;

    public RetryCondition() {
    }

    public RetryCondition(Class clazz, String message) {
        this.className = clazz.getName();
        this.message = message;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
