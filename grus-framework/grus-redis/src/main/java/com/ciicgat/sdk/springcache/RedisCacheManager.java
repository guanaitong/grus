/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.redis.config.SpringRedisConfCreator;
import com.ciicgat.sdk.util.system.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by August.Zhou on 2017/9/5 15:45.
 */
public class RedisCacheManager implements CacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheManager.class);
    private final RedisCacheConfig redisCacheConfig;
    private Set<String> names = new HashSet<>();
    private ConcurrentHashMap<String, Cache> redisCacheMap = new ConcurrentHashMap<>();
    private final RedisConnectionFactory redisConnectionFactory;
    private volatile LocalCacheEvictMessageListener localCacheEvictMessageListener;
    private byte[] channel = null;
    private final CacheRefresher cacheRefresher;

    public RedisCacheManager(RedisCacheConfig redisCacheConfig) {
        this.redisCacheConfig = Objects.requireNonNull(redisCacheConfig);
        Objects.requireNonNull(redisCacheConfig.getPrefix());
        Objects.requireNonNull(redisCacheConfig.getSerializer());
        Objects.requireNonNull(redisCacheConfig.getCacheConfigFunc());
        Objects.requireNonNull(redisCacheConfig.getCacheRefresher());
        RedisSetting redisSetting = redisCacheConfig.getRedisSetting();
        if (redisCacheConfig.getRedisSetting() == null) {
            redisSetting = SpringRedisConfCreator.getDefaultRedisSetting();
        }
        Objects.requireNonNull(redisCacheConfig);
        this.cacheRefresher = redisCacheConfig.getCacheRefresher();
        this.redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
    }


    @Override
    public Cache getCache(String name) {
        Cache redisCache = redisCacheMap.get(name);
        if (null != redisCache) {
            return redisCache;
        }
        synchronized (this) {
            redisCache = redisCacheMap.get(name);
            if (null != redisCache) {
                return redisCache;
            }
            names.add(name);
            CacheConfig cacheConfig = this.redisCacheConfig.getCacheConfig(name);
            Cache wrapperCache = cacheConfig.newCache(name, this);
            redisCache = redisCacheMap.putIfAbsent(name, wrapperCache);
            if (null == redisCache) {
                redisCache = wrapperCache;
            }
            return redisCache;
        }
    }


    void sendEvictMessage(Object key, String name) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            connection.publish(getChannel(), new LocalCacheEvictMessage(key, name, Systems.HOST_IP).toBytes());
        } catch (Exception e) {
            LOGGER.error("sendEvictMessage,key:" + key + ",name:" + name, e);
        }
    }

    private byte[] getChannel() {
        if (channel == null) {
            if (StringUtils.hasLength(redisCacheConfig.getChannel())) {
                channel = Bytes.toBytes("GRC-CHANNEL_".concat(redisCacheConfig.getChannel()));
            } else {
                channel = Bytes.toBytes("GRC-CHANNEL_".concat(Systems.APP_NAME));
            }
        }
        return channel;
    }

    public final CacheRefresher getCacheRefresher() {
        return this.cacheRefresher;
    }

    /**
     * 所有的cache，共享一个listener
     */
    void initMessageListener() {
        if (localCacheEvictMessageListener == null) {
            synchronized (this) {
                if (localCacheEvictMessageListener == null) {
                    this.localCacheEvictMessageListener = new LocalCacheEvictMessageListener();
                    redisConnectionFactory.getConnection().subscribe(this.localCacheEvictMessageListener, getChannel());
                }
            }
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return names;
    }

    public RedisCacheConfig getRedisCacheConfig() {
        return redisCacheConfig;
    }

    public final RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    public class LocalCacheEvictMessageListener implements MessageListener {

        public LocalCacheEvictMessageListener() {
        }

        @Override
        public void onMessage(Message message, byte[] pattern) {
            try {
                LocalCacheEvictMessage cacheEvictMessage = LocalCacheEvictMessage.fromBytes(message.getBody());
                Cache cache = redisCacheMap.get(cacheEvictMessage.getName());
                if ((cache instanceof ILocalCache) && !Objects.equals(Systems.HOST_IP, cacheEvictMessage.getIp())) {
                    ((ILocalCache) cache).clearLocal(cacheEvictMessage.getKey());
                }
            } catch (Exception e) {
                LOGGER.error("onMessage", e);
            }
        }
    }


}
