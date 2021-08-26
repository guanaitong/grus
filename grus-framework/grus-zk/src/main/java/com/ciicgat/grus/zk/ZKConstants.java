/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/26 11:34
 * @Description:
 */
public interface ZKConstants {
    Integer RETRY_MAX_MS = 1000;
    Integer RETRY_INTERVAL_MS = 5;
    Integer CONNECT_TIMEOUT_MS = 10000;
    Integer SESSION_TIMEOUT_MS = 6000;

    String LOCK_PATH = "LOCK";
    String IDGEN_PATH = "IDGEN";
}
