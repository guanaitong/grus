/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.config;

import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.lang.tool.Bytes;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.function.Function;

/**
 * @Author: August.Zhou
 * @Date: 2020-12-16 10:55
 */
public class SpringRedisConfCreatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRedisConfCreatorTest.class);

    @Test
    public void testStandalone() {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("grus-demo");
        RedisSetting redisSetting = configCollection.getBean("redis-config.json", RedisSetting.class);

        RedisConnectionFactory redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
        byte[] key = Bytes.toBytes(Math.random());
        byte[] value = Bytes.toBytes(Math.random());

        execute(redisConnectionFactory, redisConnection -> redisConnection.set(key, value));
        byte[] value2 = execute(redisConnectionFactory, redisConnection -> redisConnection.get(key));
        Assert.assertArrayEquals(value, value2);
    }


    //    @Test
    public void testStandalone1() {
        RedisSetting redisSetting = new RedisSetting();
        redisSetting.setDb(0);
        redisSetting.setType(0);

        RedisSetting.StandaloneConfig standaloneConfig = new RedisSetting.StandaloneConfig();
        standaloneConfig.setHost("172.24.120.31");
        standaloneConfig.setPort(6379);
        redisSetting.setStandalone(standaloneConfig);


        RedisConnectionFactory redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
        while (true) {
            Threads.sleepSeconds(1);
            long start = System.currentTimeMillis();
            try {
                byte[] key = Bytes.toBytes(Math.random());
                byte[] value = Bytes.toBytes(Math.random());

                execute(redisConnectionFactory, redisConnection -> redisConnection.set(key, value));
                byte[] value2 = execute(redisConnectionFactory, redisConnection -> redisConnection.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("cost {}", System.currentTimeMillis() - start);
        }
    }

    //    @Test
    public void testSentinel1() {
        RedisSetting redisSetting = new RedisSetting();
        redisSetting.setDb(0);
        redisSetting.setPassword("jifenpay");
        redisSetting.setType(1);

        RedisSetting.SentinelConfig sentinelConfig = new RedisSetting.SentinelConfig();
        sentinelConfig.setMaster("GAT-DEVOFC-HA-SENTINEL-REDIS");
        sentinelConfig.setNodes("10.101.11.91:18000,10.101.11.91:18001,10.101.11.91:18002");

        redisSetting.setSentinel(sentinelConfig);


        RedisConnectionFactory redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
        while (true) {
            Threads.sleepSeconds(1);
            long start = System.currentTimeMillis();
            try {
                byte[] key = Bytes.toBytes(Math.random());
                byte[] value = Bytes.toBytes(Math.random());

                execute(redisConnectionFactory, redisConnection -> redisConnection.set(key, value));
                byte[] value2 = execute(redisConnectionFactory, redisConnection -> redisConnection.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("cost {}", System.currentTimeMillis() - start);
        }
    }


    private static <T> T execute(RedisConnectionFactory redisConnectionFactory, Function<RedisConnection, T> callback) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            return callback.apply(connection);
        }
    }
}
