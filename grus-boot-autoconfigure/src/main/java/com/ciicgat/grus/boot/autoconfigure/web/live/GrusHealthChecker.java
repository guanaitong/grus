/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.live;

public interface GrusHealthChecker {

    default String isLive() {
        return "app is working";
    }
}
