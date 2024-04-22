/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkIdc;
import com.ciicgat.sdk.util.system.WorkRegion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by August.Zhou on 2024/3/20 14:02
 */
public class StandaloneChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneChecker.class);

    //for unite test
    @VisibleForTesting
    static boolean enableCheck = true;

    public static void check(RedisSetting redisSetting) {
        if (!enableCheck) {
            return;
        }
        //由于lyra环境使用的是阿里云的高可用redis，所以这里不进行检查
        if (WorkRegion.getCurrentWorkRegion().getWorkIdc() == WorkIdc.LYRA) {
            return;
        }
        Map<String, List<String>> whiteList = GlobalGconfConfig.getConfig().getBean("allow_standalone_redis.json", content -> JSON.parse(content, new TypeReference<>() {
        }));
        if (!whiteList.containsKey(Systems.APP_NAME)) {
            return;
        }
        if (whiteList.get(Systems.APP_NAME).contains(redisSetting.getName())) {
            return;
        }
        LOGGER.warn("!!!YOU ARE USE STANDALONE REDIS,THIS IS FORBIDDEN!!!");
        LOGGER.warn("!!!您正在使用单点的redis，禁止该行为，请及时迁移!!!");
        throw new UnsupportedOperationException("STANDALONE REDIS DO NOT SUPPORT");
    }
}
