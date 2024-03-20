/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.sdk.util.system.WorkIdc;
import com.ciicgat.sdk.util.system.WorkRegion;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by August.Zhou on 2024/3/20 14:02
 */
public class StandaloneChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneChecker.class);

    //for unite test
    @VisibleForTesting
    static boolean enableCheck = true;

    public static void check() {
        if (!enableCheck) {
            return;
        }
        //由于lyra环境使用的是阿里云的高可用redis，所以这里不进行检查
        if (WorkRegion.getCurrentWorkRegion().getWorkIdc() == WorkIdc.LYRA) {
            return;
        }
        LOGGER.warn("!!!YOU ARE USE STANDALONE REDIS,THIS IS FORBIDDEN!!!");
        LOGGER.warn("!!!您正在使用单点的redis，禁止该行为，请及时迁移!!!");
        throw new UnsupportedOperationException("STANDALONE REDIS DO NOT SUPPORT");
    }
}
