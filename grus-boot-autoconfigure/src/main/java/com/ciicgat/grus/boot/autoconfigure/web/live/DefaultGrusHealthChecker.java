/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.live;

import com.ciicgat.grus.boot.autoconfigure.core.AppName;

public class DefaultGrusHealthChecker implements GrusHealthChecker {

    @AppName
    private String appName;

    @Override
    public String isLive() {
        return String.format("%s is working", appName);
    }
}
