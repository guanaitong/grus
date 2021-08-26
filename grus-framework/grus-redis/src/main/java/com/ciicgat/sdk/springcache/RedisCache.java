/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.lang.tool.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by August.Zhou on 2017/9/5 15:46.
 */
public class RedisCache<R extends CacheConfig.Redis> extends AbstractCache<R> implements IRedisCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);
    protected final RedisConnectionFactory redisConnectionFactory;
    protected final String prefix;
    private final RedisKeyListener redisKeyListener;
    protected final RedisSerializer<Object> valueSerializer;

    public RedisCache(String name, RedisCacheManager redisCacheManager, R config) {
        super(name, redisCacheManager, config);
        this.redisConnectionFactory = redisCacheManager.getRedisConnectionFactory();
        final RedisCacheConfig redisCacheConfig = redisCacheManager.getRedisCacheConfig();
        this.prefix = redisCacheConfig.getPrefix().toUpperCase() + name.toUpperCase() + "_";
        this.redisKeyListener = redisCacheConfig.getRedisKeyListener();
        // 是否使用Gzip压缩
        this.valueSerializer = Optional.ofNullable(config.getUseGzip()).orElse(redisCacheConfig.isUseGzip()) ? new GzipRedisSerializer(redisCacheConfig.getSerializer()) : redisCacheConfig.getSerializer();
    }

    protected <T> T execute(Function<RedisConnection, T> callback) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            return callback.apply(connection);
        }
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    private byte[] makeKey(Object key) {
        return Bytes.toBytes(this.prefix + key);
    }

    @Override
    public ValueWrapper get(Object key) {
        Object o = getValue(key);
        if (o == AbstractCache.NULL) {
            return new SimpleValueWrapper(null);
        }
        return o == null ? null : new SimpleValueWrapper(o);
    }


    protected Object getValue(Object key) {
        Object o = null;
        try {
            byte[] redisKey = makeKey(key);
            byte[] redisValue = execute(redisConnection -> redisConnection.get(redisKey));
            if (redisValue == null) {
                return null;
            }
            if (redisValue.length == 0) {
                return AbstractCache.NULL;
            }
            o = valueSerializer.deserialize(redisValue);
        } catch (Exception e) {
            LOGGER.warn("redis error,name= " + name + ",key=" + key, e);
        }
        return o;
    }

    @Override
    public long ttl(Object key) {
        byte[] redisKey = makeKey(key);
        return execute(redisConnection -> redisConnection.ttl(redisKey)).longValue();
    }

    @Override
    public void put(final Object key, final Object value) {
        try {
            byte[] redisKey = makeKey(key);
            byte[] redisValue;
            if (value != null) {
                redisValue = valueSerializer.serialize(value);
            } else {
                redisValue = Bytes.EMPTY_BYTE_ARRAY;
            }
            Boolean v = execute(redisConnection -> {
                if (redisValue.length == 0) {
                    return redisConnection.setEx(redisKey, 30, redisValue);
                }
                if (config.getExpireSeconds() == 0) {
                    return redisConnection.set(redisKey, redisValue);
                }
                return redisConnection.setEx(redisKey, config.getExpireSeconds(), redisValue);
            });
            redisKeyListener.onPut(redisKey);
            LOGGER.debug("保存缓存key：{} 的结果是：{}", this.prefix + key, v);
        } catch (Exception e) {
            LOGGER.warn("redis error,name= " + name + ",key=" + key, e);
        }
    }


    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public void evict(Object key) {
        byte[] redisKey = makeKey(key);
        try {
            execute(redisConnection -> redisConnection.del(redisKey));
        } catch (Exception e) {
            LOGGER.warn("redis error,name= " + name + ",key=" + key, e);
        }
        redisKeyListener.onDelete(redisKey);
    }

    @Override
    public void clear() {

    }


}
