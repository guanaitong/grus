/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.lang.tool.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Objects;

/**
 * Created by August.Zhou on 2017/9/5 15:46.
 */
public class RedisCache<R extends CacheConfig.Redis> extends AbstractCache<R> implements IRedisCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);
    protected final String prefix;


    public RedisCache(String name, RedisCacheManager redisCacheManager, R config) {
        super(name, redisCacheManager, config);
        final RedisCacheConfig redisCacheConfig = redisCacheManager.getRedisCacheConfig();
        this.prefix = redisCacheConfig.getPrefix().toUpperCase() + name.toUpperCase() + "_";

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

    @Override
    public void putNewValue(Object key, Object value) {
        putIgnoreException(key, value);
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
    }


    @Override
    protected void evict0(Object key) {
        byte[] redisKey = makeKey(key);
        execute(redisConnection -> redisConnection.del(redisKey));
    }


    @Override
    protected void clear0() {
        execute(connection -> {
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(prefix + "*").count(64).build());
            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                connection.del(key);
                LOGGER.info("delete redisKey cache {},key {}", this.name, Bytes.toString(key));
            }
            return null;
        });
    }


}
