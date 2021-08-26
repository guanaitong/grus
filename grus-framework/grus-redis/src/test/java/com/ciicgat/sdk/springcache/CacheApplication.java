/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.springcache.refresh.FrequencyCacheRefresher;
import com.ciicgat.sdk.springcache.refresh.NearExpiredCacheRefresher;
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

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2018-11-15 13:20.
 */
@SpringBootApplication
@ComponentScan("com.ciicgat.sdk.springcache")
@EnableCaching
public class CacheApplication {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Threads.newDaemonThreadFactory("auto-refresh-cache"));


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

    /**
     * 默认缓存管理器（redis）
     *
     * @param redisSetting
     * @return
     */
    @Bean(name = {"cacheManager1"})
    @Primary
    public CacheManager cacheManager1(RedisSetting redisSetting) {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        redisCacheConfig.setRedisSetting(redisSetting);
        redisCacheConfig.setPrefix("GRUS_DEMO_1_");
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> {
            switch (name) {
                case "uidCache":
                    return CacheConfig.redis().setExpireSeconds(600);
            }

            return CacheConfig.redis();
        });
        return new RedisCacheManager(redisCacheConfig);
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
        redisCacheConfig.setPrefix("GRUS_DEMO_2_");
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> {
            switch (name) {
                case "uidCache":
                    return CacheConfig.localRedis().setExpireSeconds(600);
            }

            return CacheConfig.localRedis();
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
        redisCacheConfig.setPrefix("GRUS_DEMO_3_");
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> {
            switch (name) {
                case "useRedisCache":
                    return CacheConfig.redis().setExpireSeconds(600);
                case "useLocalCacheSerialize":
                    return CacheConfig.localRedis().setExpireSeconds(600).setLocalExpireSeconds(120).setSerialize(true);
                case "useLocalCache":
                    return CacheConfig.local().setExpireSeconds(60);
                case "useLocalCacheNoExpire":
                    return CacheConfig.local().setExpireSeconds(0);
            }

            return CacheConfig.localRedis();
        });
        redisCacheConfig.setCacheRefresher(new FrequencyCacheRefresher(EXECUTOR_SERVICE, 3));
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
        redisCacheConfig.setPrefix("GRUS_DEMO_4_");
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> CacheConfig.redis());
        redisCacheConfig.setCacheRefresher(new NearExpiredCacheRefresher(EXECUTOR_SERVICE, 3600));
        return new RedisCacheManager(redisCacheConfig);
    }

    @Bean(name = "nearExpiredAsyncCache")
    public Cache nearExpiredAsyncCache(@Qualifier("cacheManager4") CacheManager cacheManager) {
        return cacheManager.getCache("nearExpiredAsyncCache");
    }
}
