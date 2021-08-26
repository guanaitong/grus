/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.redis;

import com.ciicgat.grus.boot.autoconfigure.core.AppName;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.redis.RedisExecutor;
import com.ciicgat.sdk.redis.RedisService;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.redis.config.SpringRedisConfCreator;
import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;

/**
 * @author August.Zhou
 * @date 2020-04-22 10:23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RedisOperations.class, RedisClient.class, RedisSetting.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class GrusRedisAutoConfiguration {

    public static final String DEFAULT_REDIS_CONFIG_KEY = "redis-config.json";

    @AppName
    private String appName;

    @Bean(name = "redisSetting")
    @ConditionalOnMissingBean(name = "redisSetting")
    public RedisSetting redisSetting() {
        return RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection(appName).getBean(DEFAULT_REDIS_CONFIG_KEY, RedisSetting.class);
    }

    @Bean
    @ConditionalOnBean(RedisSetting.class)
    @ConditionalOnClass(Jedis.class)
    @ConditionalOnMissingBean(name = "redisExecutor")
    public RedisExecutor redisExecutor(@Qualifier("redisSetting") RedisSetting redisSetting) {
        return redisSetting.newRedisExecutor();
    }

    @Bean
    @ConditionalOnBean(RedisSetting.class)
    @ConditionalOnClass({RedisOperations.class, RedisConnectionFactory.class})
    @ConditionalOnMissingBean(name = "redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory(@Qualifier("redisSetting") RedisSetting redisSetting) {
        return SpringRedisConfCreator.newRedisConnectionFactory(redisSetting);
    }

    /**
     * 初始化自定义 redisService 时需要
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @return StringRedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StringRedisTemplate.class)
    public RedisService redisService(StringRedisTemplate stringRedisTemplate) {
        return new RedisService(stringRedisTemplate);
    }

}
