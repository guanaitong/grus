/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import com.ciicgat.sdk.redis.SpringRedisConfCreator;
import com.ciicgat.sdk.redis.config.RedisSetting;
import io.lettuce.core.ClientOptions;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Created by August.Zhou on 2017/9/5 15:45.
 */
public class RedisCacheManager implements CacheManager, MeterBinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheManager.class);
    private final RedisCacheConfig redisCacheConfig;
    private ConcurrentHashMap<String, AbstractCache<?>> redisCacheMap = new ConcurrentHashMap<>();
    private final RedisConnectionFactory redisConnectionFactory;
    private volatile LocalCacheEvictMessageListener localCacheEvictMessageListener;
    private final byte[] channel;
    private final String id;
    private MeterRegistry meterRegistry;
    private volatile Executor refreshExecutor;

    public RedisCacheManager(RedisCacheConfig redisCacheConfig) {
        this.redisCacheConfig = Objects.requireNonNull(redisCacheConfig);
        Objects.requireNonNull(redisCacheConfig.getPrefix());
        Objects.requireNonNull(redisCacheConfig.getSerializer());
        Objects.requireNonNull(redisCacheConfig.getCacheConfigFunc());
        RedisSetting redisSetting = redisCacheConfig.getRedisSetting();
        if (redisCacheConfig.getRedisSetting() == null) {
            redisSetting = SpringRedisConfCreator.getDefaultRedisSetting();
        }
        Objects.requireNonNull(redisCacheConfig);
        this.redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true, ClientOptions.DisconnectedBehavior.REJECT_COMMANDS);
        this.id = new SessionIdGenerator().generateSessionId(8);
        this.channel = Bytes.toBytes(this.redisCacheConfig.getPrefix() + "_CHANNEL");
        LOGGER.info("this cacheManagerId is {}", this.id);
    }


    @Override
    public Cache getCache(String name) {
        AbstractCache<?> redisCache = redisCacheMap.get(name);
        if (null != redisCache) {
            return redisCache;
        }
        synchronized (this) {
            redisCache = redisCacheMap.get(name);
            if (null != redisCache) {
                return redisCache;
            }
            return redisCacheMap.computeIfAbsent(name, cacheName -> {
                CacheConfig<?> cacheConfig = redisCacheConfig.getCacheConfig(cacheName);
                AbstractCache<?> newCache = cacheConfig.newCache(cacheName, RedisCacheManager.this);
                cacheBindTo(newCache);
                return newCache;
            });
        }
    }


    void sendEvictMessage(String key, String name) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            connection.publish(this.channel, new LocalCacheEvictMessage(key, name, id).toBytes());
        } catch (Exception e) {
            LOGGER.error("sendEvictMessage,key:" + key + ",name:" + name, e);
        }
    }


    Executor getRefreshExecutor() {
        if (refreshExecutor == null) {
            synchronized (this) {
                if (refreshExecutor == null) {
                    refreshExecutor = this.redisCacheConfig.getRefreshExecutor().get();
                }
            }
        }
        return refreshExecutor;
    }

    /**
     * 所有的cache，共享一个listener
     */
    void initMessageListener() {
        if (localCacheEvictMessageListener == null) {
            synchronized (this) {
                if (localCacheEvictMessageListener == null) {
                    LocalCacheEvictMessageListener tempListener = new LocalCacheEvictMessageListener();
                    redisConnectionFactory.getConnection().subscribe(tempListener, this.channel);
                    this.localCacheEvictMessageListener = tempListener;
                }
            }
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return redisCacheMap.keySet();
    }

    public RedisCacheConfig getRedisCacheConfig() {
        return redisCacheConfig;
    }

    public final RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.meterRegistry = registry;
        for (Map.Entry<String, AbstractCache<?>> cacheEntry : redisCacheMap.entrySet()) {
            cacheBindTo(cacheEntry.getValue());
        }
    }

    private void cacheBindTo(AbstractCache<?> newCache) {
        if (meterRegistry != null && !newCache.isBind()) {
            Tags tags = Tags.of("name", newCache.getName());
            new GrusCacheMeterBinder(newCache, newCache.getName(), tags).bindTo(meterRegistry);
            newCache.setBind(true);
        }
    }

    public class LocalCacheEvictMessageListener implements MessageListener {

        public LocalCacheEvictMessageListener() {
            //nothing
        }

        @Override
        public void onMessage(Message message, byte[] pattern) {
            try {
                LocalCacheEvictMessage cacheEvictMessage = LocalCacheEvictMessage.fromBytes(message.getBody());
                Cache cache = redisCacheMap.get(cacheEvictMessage.getName());
                if ((cache instanceof ILocalCache) && !Objects.equals(id, cacheEvictMessage.getId())) {
                    ((ILocalCache) cache).clearLocal(cacheEvictMessage.getKey());
                }
            } catch (Exception e) {
                LOGGER.error("onMessage", e);
            }
        }
    }


}
