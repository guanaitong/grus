/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.redis.config.RedisSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.commands.JedisCommands;

/**
 * Created by August.Zhou on 2023/7/21 15:07
 */
public class RedisTimeoutTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRedisConfCreatorTest.class);

    public static void main(String[] args) {
        test2();
    }

    private static void test1() {
        RedisSetting redisSetting = new RedisSetting();
        redisSetting.setDb(0);
        redisSetting.setType(0);

        RedisSetting.StandaloneConfig standaloneConfig = new RedisSetting.StandaloneConfig();
        standaloneConfig.setHost("10.101.11.46");
        standaloneConfig.setPort(6379);
        redisSetting.setStandalone(standaloneConfig);
        RedisExecutor redisExecutor = redisSetting.newRedisExecutor();

        for (int i = 0; i < 1000; i++) {
            final int j = i;
            redisExecutor.execute(new RedisAction<Object>() {
                @Override
                public Object apply(JedisCommands jedis) {
                    jedis.set("123", "xxx" + String.valueOf(j));
                    System.out.println(jedis.get("123"));
                    return null;
                }
            });
            Threads.sleepSeconds(1);
        }

        Threads.sleepSeconds(100);

        LOGGER.info("start");
        redisExecutor.execute(new RedisAction<Object>() {
            @Override
            public Object apply(JedisCommands jedis) {
                jedis.set("123", "yyy");
                System.out.println(jedis.get("123"));
                return null;
            }
        });

        LOGGER.info("end");
    }

    private static void test2() {
        RedisSetting redisSetting = new RedisSetting();
        redisSetting.setDb(0);
        redisSetting.setType(0);

        RedisSetting.StandaloneConfig standaloneConfig = new RedisSetting.StandaloneConfig();
        standaloneConfig.setHost("10.101.11.46");
        standaloneConfig.setPort(6379);
        redisSetting.setStandalone(standaloneConfig);

        RedisConnectionFactory redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
//        for (int i = 0; i < 1000; i++) {
//            stringRedisTemplate.opsForValue().set("123", i + "");
//            Threads.sleepSeconds(1);
//        }

        stringRedisTemplate.opsForValue().set("123", "xxxx");

//        Threads.sleepSeconds(120);
        Threads.sleepSeconds(10);
        LOGGER.info("start1");
        try {
            stringRedisTemplate.opsForValue().set("123", "xxxx");
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        LOGGER.info("end1");
        Threads.sleepSeconds(60);

        LOGGER.info("start2");
        try {
            stringRedisTemplate.opsForValue().set("123", "xxxx");
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        LOGGER.info("end2");
    }
}
