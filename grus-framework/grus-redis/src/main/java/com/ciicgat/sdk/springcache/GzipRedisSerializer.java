/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.apache.commons.io.IOUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 对于一些大key、value，可以使用gzip压缩
 * Created by August.Zhou on 2020-04-14 13:01.
 */
public class GzipRedisSerializer implements RedisSerializer {

    private final RedisSerializer redisSerializer;

    public GzipRedisSerializer(RedisSerializer redisSerializer) {
        this.redisSerializer = redisSerializer;
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        byte[] serialize = redisSerializer.serialize(o);
        if (serialize == null || serialize.length == 0) {
            return serialize;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
            gzipOutputStream.write(serialize);
            gzipOutputStream.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("gzip serialize failed", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            GZIPInputStream inputStream = new GZIPInputStream(byteArrayInputStream);
            return redisSerializer.deserialize(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new SerializationException("gzip deserialize failed", e);
        }
    }
}
