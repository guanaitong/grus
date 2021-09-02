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

import java.util.Objects;
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
        boolean useGzip = Objects.isNull(config.getUseGzip()) ? redisCacheConfig.isUseGzip() : config.getUseGzip().booleanValue();
        this.valueSerializer = useGzip ? new GzipRedisSerializer(redisCacheConfig.getSerializer()) : redisCacheConfig.getSerializer();
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
    protected final ValueWrapper get0(Object key) {
        BytesValue bytesValue = getValue(key);
        if (Objects.isNull(bytesValue)) {
            return null;
        }
        return new SimpleValueWrapper(bytesValue == AbstractCache.NULL_BYTES_VALUE ? null : bytesValue.getValue());
    }


    protected BytesValue getValue(Object key) {
        byte[] redisKey = makeKey(key);
        byte[] redisValue = execute(redisConnection -> redisConnection.get(redisKey));
        if (redisValue == null) {
            return null;
        }
        if (redisValue.length == 0) {
            return NULL_BYTES_VALUE;
        }
        return new BytesValue(redisValue, valueSerializer.deserialize(redisValue));
    }

    @Override
    public long ttl(Object key) {
        byte[] redisKey = makeKey(key);
        return execute(redisConnection -> redisConnection.ttl(redisKey)).longValue();
    }

    @Override
    protected void put0(final Object key, final Object value) {
        BytesValue bytesValue = value == null ? NULL_BYTES_VALUE : new BytesValue(valueSerializer.serialize(value), value);
        saveRedisCache(key, bytesValue);
    }

    protected final void saveRedisCache(final Object key, final BytesValue bytesValue) {
        byte[] redisKey = makeKey(key);
        execute(redisConnection -> {
            var redisValue = bytesValue.getBytes();
            if (config.getExpireSeconds() == 0) {
                return redisConnection.set(redisKey, redisValue);
            }
            return redisConnection.setEx(redisKey, config.getExpireSeconds(), redisValue);
        });
        redisKeyListener.onPut(redisKey);
    }


    @Override
    protected void evict0(Object key) {
        byte[] redisKey = makeKey(key);
        execute(redisConnection -> redisConnection.del(redisKey));
        redisKeyListener.onDelete(redisKey);
    }

    @Override
    public void clear() {

    }


}
