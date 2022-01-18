/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.springcache.event.extend.ClearAfterHttpCompletionCacheChangeListener;
import com.ciicgat.sdk.springcache.refresh.RefreshPolicy;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * Created by August.Zhou on 2018-11-15 13:20.
 */
@SpringBootApplication(proxyBeanMethods = false)
@ComponentScan("com.ciicgat.sdk.springcache")
@EnableCaching
public class CacheApplication {


    public static void main(String[] args) {
        new SpringApplicationBuilder(CacheApplication.class)
                .run(args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMinutes(10)).setReadTimeout(Duration.ofMinutes(10)).build();
    }

    @Bean
    public RedisSetting redisSetting() {
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("grus-demo");
        return configCollection.getBean("redis-config.json", RedisSetting.class);
    }

    @Bean
    public ClearAfterHttpCompletionCacheChangeListener clearAfterHttpCompletionCacheChangeListener() {
        return new ClearAfterHttpCompletionCacheChangeListener();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(ClearAfterHttpCompletionCacheChangeListener clearAfterHttpCompletionCacheChangeListener) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                        clearAfterHttpCompletionCacheChangeListener.clear();
                    }
                });
            }
        };
    }

    /**
     * 默认缓存管理器（redis）
     *
     * @param redisSetting
     * @return
     */
    @Bean(name = {"cacheManager1"})
    @Primary
    public CacheManager cacheManager1(RedisSetting redisSetting, ClearAfterHttpCompletionCacheChangeListener clearAfterHttpCompletionCacheChangeListener) {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        redisCacheConfig.setRedisSetting(redisSetting);
        redisCacheConfig.setPrefix("GRUS_DEMO_1_" + RandomStringUtils.random(2));
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setCacheConfigFunc(name -> {
            switch (name) {
                case "uidCache":
                    return CacheConfig.redis().setUseGzip(true).setSerializer(RedisSerializer.json()).setExpireSeconds(600);
            }

            return CacheConfig.redis();
        });
        redisCacheConfig.setCacheChangeListener(clearAfterHttpCompletionCacheChangeListener);
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisCacheConfig);
        redisCacheManager.bindTo(new SimpleMeterRegistry());
        return redisCacheManager;
    }

    /**
     * 开启二级缓存缓存管理器（redis+caffeine）
     *
     * @param redisSetting
     * @return
     */
    @Bean(name = {"cacheManager2"})
    public CacheManager cacheManager2(RedisSetting redisSetting) {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        redisCacheConfig.setRedisSetting(redisSetting);
        redisCacheConfig.setPrefix("GRUS_DEMO_2_" + RandomStringUtils.random(2));
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> {
            switch (name) {
                case "uidCache":
                    return CacheConfig.l2().setExpireSeconds(600);
            }
            return CacheConfig.l2();
        });
        return new RedisCacheManager(redisCacheConfig);
    }

    /**
     * 开启二级缓存缓存管理器（redis+caffeine）
     * Cache自定义
     *
     * @param redisSetting
     * @return
     */
    @Bean(name = {"cacheManager3"})
    public CacheManager cacheManager3(RedisSetting redisSetting) {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        redisCacheConfig.setRedisSetting(redisSetting);
        redisCacheConfig.setPrefix("GRUS_DEMO_3_" + RandomStringUtils.random(2));
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> {
            switch (name) {
                case "useRedisCache":
                    return CacheConfig.redis().setExpireSeconds(600);
                case "useLocalCacheSerialize":
                    return CacheConfig.l2().setExpireSeconds(600).setLocalExpireSeconds(120).setSerialize(true);
                case "useLocalCache":
                    return CacheConfig.local().setCacheNull(true).setExpireSeconds(60);
                case "useLocalCacheNoExpire":
                    return CacheConfig.local().setExpireSeconds(0);
            }

            return CacheConfig.l2().setSerialize(false);
        });
        redisCacheConfig.setRefreshExecutor(() -> Runnable::run);
        redisCacheConfig.setRefreshPolicy(RefreshPolicy.frequency(2));
        return new RedisCacheManager(redisCacheConfig);
    }

    @Bean(name = "frequencyAsyncCache")
    public Cache frequencyAsyncCache(@Qualifier("cacheManager3") CacheManager cacheManager) {
        return cacheManager.getCache("frequencyAsyncCache");
    }


    @Bean(name = {"cacheManager4"})
    public CacheManager cacheManager4(RedisSetting redisSetting) {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        redisCacheConfig.setRedisSetting(redisSetting);
        redisCacheConfig.setPrefix("GRUS_DEMO_4_" + RandomStringUtils.random(2));
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> CacheConfig.redis());
        redisCacheConfig.setRefreshPolicy(RefreshPolicy.random(0.3));
        redisCacheConfig.setRefreshExecutor(() -> Runnable::run);
        return new RedisCacheManager(redisCacheConfig);
    }

    @Bean(name = "randomAsyncCache")
    public Cache randomAsyncCache(@Qualifier("cacheManager4") CacheManager cacheManager) {
        return cacheManager.getCache("randomAsyncCache");
    }
}
