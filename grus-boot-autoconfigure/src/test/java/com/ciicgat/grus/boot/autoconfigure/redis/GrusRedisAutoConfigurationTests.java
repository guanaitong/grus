/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.redis;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import com.ciicgat.sdk.redis.RedisExecutor;
import com.ciicgat.sdk.redis.RedisService;
import com.ciicgat.sdk.redis.config.RedisSetting;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2020-04-22 10:38.
 */
public class GrusRedisAutoConfigurationTests {

    private static final String TEST_REDIS_KEY = "test-for-java";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(GrusRedisAutoConfiguration.class);

    @Test
    public void test() {
        final String randomValue = System.currentTimeMillis() + "";

        this.contextRunner
                .withSystemProperties()
                .withPropertyValues("spring.application.name=grus-demo")
                .run(context -> {
                    RedisSetting redisSetting = context.getBean(RedisSetting.class);
                    assertThat(redisSetting).isInstanceOf(RedisSetting.class);
                    RedisService redisService = context.getBean(RedisService.class);
                    StringRedisTemplate stringRedisTemplate = context.getBean(StringRedisTemplate.class);
                    RedisExecutor redisExecutor = context.getBean(RedisExecutor.class);
                    redisService.set(TEST_REDIS_KEY, randomValue);
                    String result = stringRedisTemplate.opsForValue().get(TEST_REDIS_KEY);
                    String resultFromExecute = redisExecutor.execute(jedis -> jedis.get(TEST_REDIS_KEY));
                    assertThat(result).isEqualTo(randomValue);
                    assertThat(resultFromExecute).isEqualTo(randomValue);
                });
    }

}
