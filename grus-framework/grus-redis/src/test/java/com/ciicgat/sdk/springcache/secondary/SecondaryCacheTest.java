/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.secondary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * Created by August.Zhou on 2021/12/17 10:56.
 */
class SecondaryCacheTest {
    private SecondaryCache secondaryCache;

    private Cache cache;


    @BeforeEach
    public void setup() {
        cache = Mockito.mock(Cache.class);
        secondaryCache = new SecondaryCache(cache);
    }

    @Test
    void get() {
    }

    @Test
    void invalidate() {
        Mockito.doNothing().when(cache).evict(Mockito.any());
        secondaryCache.invalidate(makeKey());
        Mockito.doThrow(new RuntimeException()).when(cache).evict(Mockito.any());
        secondaryCache.invalidate(makeKey());
        Mockito.verify(cache, Mockito.times(2)).evict(Mockito.any());
    }

    private static String makeKey() {
        return String.valueOf(Math.random());
    }

    @Test
    public void testGet_hit_secondaryL1Cache() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        //命中了一级缓存
        Mockito.when(cache.get(key)).thenReturn(new SimpleValueWrapper(person.getId()));

        Person person1 = secondaryCache.get(key, id -> person, () -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(3);
            person3.setName("huxuan2");
            return new IdObject<>(person3.getId(), person3);
        });

        Assertions.assertSame(person, person1);
    }

    @Test
    public void testGet_hit_secondaryL2Cache() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        //未命中一级缓存
        Mockito.when(cache.get(key)).thenReturn(null);

        Person person1 = secondaryCache.get(key, (id) -> {
            //这个方法不执行
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(3);
            person3.setName("huxuan2");
            return person3;

        }, () -> new IdObject<>(person.getId(), person)); //命中二级缓存

        Assertions.assertSame(person, person1);
    }

    @Test
    public void testGet_miss_Cache_withException() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        Mockito.when(cache.get(key)).thenReturn(null);

        Assertions.assertThrows(RuntimeException.class, () -> secondaryCache.get(key, (id) -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(4);
            person3.setName("huxuan2");
            return person3;
        }, () -> {
            throw new RuntimeException("DB error");
        }));
    }

    @Test
    public void testGet_hit_secondaryL1Cache_withException() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        Mockito.when(cache.get(key)).thenReturn(new SimpleValueWrapper(person.getId()));

        Assertions.assertThrows(RuntimeException.class, () -> secondaryCache.get(key, (id) -> {
            throw new RuntimeException("异常");
        }, () -> new IdObject<>(person.getId(), person)));

    }

    @Test
    public void testGet_Cache_withGetException() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        try {
            Mockito.when(cache.get(key)).thenThrow(new RuntimeException());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        Person person1 = secondaryCache.get(key, (id) -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(4);
            person3.setName("huxuan2");
            return person3;
        }, () -> new IdObject<>(person.getId(), person));

        Assertions.assertSame(person, person1);
    }

    @Test
    public void testGet_Cache_withSaveException() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        try {
            Mockito.doThrow(new RuntimeException()).when(cache).put(key, person.getId());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        Person person1 = secondaryCache.get(key, (id) -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(4);
            person3.setName("huxuan2");
            return person3;
        }, () -> new IdObject<>(person.getId(), person));

        Assertions.assertSame(person, person1);

        Person person2 = secondaryCache.get(key, (id) -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(4);
            person3.setName("huxuan2");
            return person3;
        }, () -> {
            final Person person3 = new Person();
            person3.setId(1);
            person3.setMemberId(3);
            person3.setName("huxuan");
            return new IdObject<>(person3.getId(), person3);
        });

        Assertions.assertNotSame(person, person2);

        Assertions.assertEquals(person.getId(), person2.getId());
        Assertions.assertEquals(person.getMemberId(), person2.getMemberId());
        Assertions.assertEquals(person.getName(), person2.getName());
    }
}
