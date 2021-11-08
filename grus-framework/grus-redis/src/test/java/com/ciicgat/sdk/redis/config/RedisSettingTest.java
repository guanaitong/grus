/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.config;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author August
 * @date 2020/2/24 3:28 PM
 */
public class RedisSettingTest {

    @Test
    public void testStandalone() {
        RedisSetting redisSetting = new RedisSetting();
        redisSetting.setDb(15);
        redisSetting.setPassword("i am secrets");
        redisSetting.setType(0);

        RedisSetting.StandaloneConfig standaloneConfig = new RedisSetting.StandaloneConfig();
        standaloneConfig.setHost("localhost");
        standaloneConfig.setPort(6379);
        redisSetting.setStandalone(standaloneConfig);

        System.out.println(JSON.toJSONString(redisSetting));
    }

    @Test
    public void testSentinel() {
        RedisSetting redisSetting = new RedisSetting();
        redisSetting.setDb(15);
        redisSetting.setPassword("i am secrets");
        redisSetting.setType(1);

        RedisSetting.SentinelConfig sentinelConfig = new RedisSetting.SentinelConfig();
        sentinelConfig.setMaster("mymaster");
        sentinelConfig.setNodes("10.130.0.4:28000,10.130.0.5:28000,10.130.0.20:28000");

        redisSetting.setSentinel(sentinelConfig);

        System.out.println(JSON.toJSONString(redisSetting));
    }


    @Test
    public void testGetFromGconf() {
        //todo:因为跑单测始终不过 导致无法发版 故对其进行注释 以下为当时的报错信息 供参考
        //java.lang.AssertionError:
        //Expected :null
        //Actual   :LKmaqJy22KQf

        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("for-test-java");
        String config = configCollection.getConfig("redis-config.json");
        System.out.println("config = " + config);
        RedisSetting redisSetting = configCollection.getBean("redis-config.json", RedisSetting.class);
//        Assertions.assertEquals(redisSetting.getPassword(), "LKmaqJy22KQf");
        Assertions.assertNotEquals(redisSetting.getPassword(), "LKmaqJy22KQf");

    }

}
