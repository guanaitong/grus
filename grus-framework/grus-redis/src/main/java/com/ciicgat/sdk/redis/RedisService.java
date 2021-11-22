/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2020-04-14 15:21.
 */
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean exists(final String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public void set(final String key, final String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String get(final String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public String getSet(final String key, final String value) {
        return stringRedisTemplate.opsForValue().getAndSet(key, value);
    }

    public void setex(final String key, final int seconds, final String value) {
        stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public Boolean setnx(final String key, final String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public Boolean setnx(final String key, final String value, final int seconds) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    public Boolean expire(final String key, final int seconds) {
        return stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public long ttl(final String key) {
        return stringRedisTemplate.getExpire(key);
    }

    public void del(final String key) {
        stringRedisTemplate.delete(key);
    }

    public void hset(final String key, final String field, final String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    public Long hlen(final String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    public String hget(final String key, final String field) {
        HashOperations<String, String, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        return stringObjectObjectHashOperations.get(key, field);
    }

    public Map<Object, Object> hgetAll(final String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public Boolean zadd(final String key, final Double score, final String member) {
        return stringRedisTemplate.opsForZSet().add(key, member, score);
    }

    public Set<ZSetOperations.TypedTuple<String>> zrevrangeWithScores(final String key) {
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    public Set<String> zrevrange(final String key) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, 0, -1);
    }

    public Double zincrby(final String key, final Double score, final String member) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, member, score);
    }

    public Long llen(final String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    public Long lpush(final String key, final String... strings) {
        return stringRedisTemplate.opsForList().leftPushAll(key, strings);
    }

    /**
     * 移除并返回列表 key 的尾元素。
     *
     * @param key
     * @return
     */
    public String rpop(final String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public String brpop(final String key) {
        return stringRedisTemplate.opsForList().rightPop(key, 0, TimeUnit.SECONDS);
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        stringRedisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return stringRedisTemplate.opsForHash().hasKey(key, item);
    }

    public Set<String> scan(String matchKey) {
        return stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>(64);
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(matchKey).count(1000).build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }

            return keysTmp;
        });
    }
}
