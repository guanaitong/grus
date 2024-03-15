/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.idgen;

import com.ciicgat.grus.idgen.WorkIdHolder;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.redis.config.RedisSetting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author August
 * @date 3/15/2024 3:13 PM
 */
class RedisWorkIdHolderTest {

    @Test
    void getId() {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("for-test-java");
        RedisSetting redisSetting = configCollection.getBean("redis-config.json", RedisSetting.class);
        WorkIdHolder workIdHolder = new RedisWorkIdHolder(redisSetting.newRedisExecutor());
        Set<Long> ids = new HashSet<>();
        var n = 100;
        for (int i = 0; i < n; i++) {

            long workId = workIdHolder.getId();
            ids.add(workId);
        }
        Assertions.assertEquals(n, ids.size());
    }
}
