/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author : August
 * @date 2020/3/23 16:06
 */
public class RedisSerializerTests {

    public static class User implements Serializable {
        private int id;
        private String name;
        private Date date;

        public User(int id, String name, Date date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public User() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return id == user.id &&
                    Objects.equals(name, user.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    public static class User2 {
        private String name;
        private Date date;

        public User2(String name, Date date) {
            this.name = name;
            this.date = date;
        }

        public User2() {
        }

        public String getName() {
            return name;
        }

        public User2 setName(String name) {
            this.name = name;
            return this;
        }

        public Date getDate() {
            return date;
        }

        public User2 setDate(Date date) {
            this.date = date;
            return this;
        }
    }


    @Test
    public void test() {
        User user = new User(1, "aa", new Date());
        RedisSerializer[] redisSerializers = new RedisSerializer[]{
                RedisSerializer.java(),
                RedisSerializer.json(),
                new GzipRedisSerializer(RedisSerializer.java()),
                new GzipRedisSerializer(RedisSerializer.json())
        };
        for (RedisSerializer redisSerializer : redisSerializers) {
            byte[] bytes = redisSerializer.serialize(user);
            System.out.println(bytes.length);
            if (redisSerializer instanceof GenericJackson2JsonRedisSerializer) {
                String s = new String(bytes);
                Assertions.assertTrue(s.contains("@class"));
                Assertions.assertTrue(s.contains(RedisSerializerTests.class.getName()));
                System.out.println(s);
            }
            Assertions.assertEquals(user, redisSerializer.deserialize(bytes));
        }
    }

    @Test
    public void testWithException() {
        User2 user = new User2("aaa", new Date());
        GzipRedisSerializer redisSerializer = new GzipRedisSerializer(RedisSerializer.java());
        Assertions.assertThrows(SerializationException.class, () -> redisSerializer.serialize(user));
    }

    @Test
    public void testWithException2() {
        GzipRedisSerializer redisSerializer = new GzipRedisSerializer(RedisSerializer.java());
        Assertions.assertThrows(SerializationException.class, () ->redisSerializer.deserialize(new byte[]{1, 2}));
    }

}
