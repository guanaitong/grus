/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.redis.config.RedisSetting;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @Author: August.Zhou
 * @Date: 2020-12-16 10:55
 */
public class RedisConnectTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConnectTest.class);

    @Test
    public void testStandalone() {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("grus-demo");
        RedisSetting redisSetting = configCollection.getBean("redis-config-standalone.json", RedisSetting.class);
        testConnect(redisSetting);
    }

    @Test
    public void testSentinel() {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("grus-demo");
        RedisSetting redisSetting = configCollection.getBean("redis-config-sentinel.json", RedisSetting.class);
        testConnect(redisSetting);
    }

    @Test
    public void testCluster() {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("grus-demo");
        RedisSetting redisSetting = configCollection.getBean("redis-config-cluster.json", RedisSetting.class);
        testConnect(redisSetting);
    }

    private void testConnect(RedisSetting redisSetting) {
        SpringRedisConfCreator.newRedisProperties(redisSetting);
        RedisConnectionFactory redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
        RedisExecutor redisExecutor = redisSetting.newRedisExecutor();
        for (int i = 0; i < 1; i++) {
            String key = RandomStringUtils.randomAlphabetic(10);
            String value = RandomStringUtils.randomAlphabetic(10);

            execute(redisConnectionFactory, redisConnection -> redisConnection.set(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8)));
            byte[] value2 = execute(redisConnectionFactory, redisConnection -> redisConnection.get(key.getBytes(StandardCharsets.UTF_8)));
            Assertions.assertEquals(value, new String(value2, StandardCharsets.UTF_8));
            execute(redisConnectionFactory, redisConnection -> redisConnection.del(key.getBytes(StandardCharsets.UTF_8)));

            String v = redisExecutor.execute(jedis -> jedis.set(key, value));
            Assertions.assertEquals(v, "OK");
            Assertions.assertEquals(value, redisExecutor.execute(jedis -> jedis.get(key)));
            redisExecutor.execute(jedis -> jedis.del(key));
        }


        CloseUtils.close(redisExecutor);
    }


    private static <T> T execute(RedisConnectionFactory redisConnectionFactory, Function<RedisConnection, T> callback) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            return callback.apply(connection);
        }
    }


    @Test
    @Disabled
    public void testClusterPublish() {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("grus-demo");
        RedisSetting redisSetting = configCollection.getBean("redis-config-cluster.json", RedisSetting.class);

        RedisConnectionFactory redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
        byte[] key = Bytes.toBytes(Math.random());
        byte[] value = Bytes.toBytes(Math.random());

        execute(redisConnectionFactory, redisConnection -> redisConnection.set(key, value));
        byte[] value2 = execute(redisConnectionFactory, redisConnection -> redisConnection.get(key));
        Assertions.assertArrayEquals(value, value2);

        List<Integer> stringList = new ArrayList<>();
        execute(redisConnectionFactory, new Function<RedisConnection, Object>() {
            @Override
            public Object apply(RedisConnection connection) {
                String prefix = "8";
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(prefix + "*").count(512).build());
                while (cursor.hasNext()) {
                    byte[] key = cursor.next();
                    try {
                        stringList.add(Integer.parseInt(Bytes.toString(key)));
                    } catch (Exception e) {

                    }

//                    System.out.println(Bytes.toString(key));
                }
                return null;
            }
        });
        Collections.sort(stringList);
        System.out.println(stringList);

        execute(redisConnectionFactory, new Function<RedisConnection, Object>() {
            @Override
            public Object apply(RedisConnection redisConnection) {
                redisConnection.subscribe(new MessageListener() {
                    @Override
                    public void onMessage(Message message, byte[] pattern) {
                        System.out.println(Bytes.toString(message.getBody()));
                    }
                }, Bytes.toBytes("test"));
                return null;
            }
        });
        System.out.println(1);

        execute(redisConnectionFactory, new Function<RedisConnection, Object>() {
            @Override
            public Object apply(RedisConnection redisConnection) {
                redisConnection.publish(Bytes.toBytes("test"), Bytes.toBytes("test11111"));
                return null;
            }
        });
    }
}
