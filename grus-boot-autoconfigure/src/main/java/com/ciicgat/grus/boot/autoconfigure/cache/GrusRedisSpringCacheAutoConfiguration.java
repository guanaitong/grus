/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.cache;

import com.ciicgat.grus.boot.autoconfigure.redis.GrusRedisAutoConfiguration;
import com.ciicgat.sdk.springcache.RedisCacheConfig;
import com.ciicgat.sdk.springcache.RedisCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Created by August.Zhou on 2019-08-21 12:50.
 */
@ConditionalOnClass({CacheManager.class, RedisConnectionFactory.class, RedisCacheManager.class})
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(GrusRedisAutoConfiguration.class)
@AutoConfigureBefore(CacheAutoConfiguration.class)
@ConditionalOnBean({CacheAspectSupport.class, RedisCacheConfig.class})
@ConditionalOnMissingBean(value = CacheManager.class, name = "cacheResolver")
public class GrusRedisSpringCacheAutoConfiguration {


    @Bean(name = {"cacheManager", "redisCacheManager"})
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedisCacheConfig redisCacheConfig) {
        return new RedisCacheManager(redisCacheConfig);
    }

}
