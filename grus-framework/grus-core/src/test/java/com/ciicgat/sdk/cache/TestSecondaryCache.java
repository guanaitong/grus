/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;

import com.ciicgat.sdk.lang.exception.CacheDataException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.Silent.class)
public class TestSecondaryCache {
    @Mock
    private LocalCache cache;

    private SecondaryCache secondaryCache;


    @Before
    public void setup() {
        secondaryCache = new SecondaryCache(cache);
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
        Mockito.when(cache.getValue(key)).thenReturn(person.getId());

        Person person1 = secondaryCache.get(key, (id) -> person, () -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(3);
            person3.setName("huxuan2");
            return new IdObject<>(person3.getId(), person3);
        });

        Assert.assertSame(person, person1);
    }

    @Test
    public void testGet_hit_secondaryL2Cache() throws CacheDataException {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        //未命中一级缓存
        Mockito.when(cache.getValue(key)).thenReturn(null);

        Person person1 = secondaryCache.get(key, (id) -> {
            //这个方法不执行
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(3);
            person3.setName("huxuan2");
            return person3;

        }, () -> new IdObject<>(person.getId(), person)); //命中二级缓存

        Assert.assertSame(person, person1);
    }

    @Test(expected = RuntimeException.class)
    public void testGet_miss_Cache_withException() throws RuntimeException {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        Mockito.when(cache.getValue(key)).thenReturn(null);

        Person person1 = secondaryCache.get(key, (id) -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(4);
            person3.setName("huxuan2");
            return person3;
        }, () -> {
            throw new RuntimeException("DB error");
        });

        Assert.assertSame(person, person1);
    }

    @Test
    public void testGet_hit_secondaryL1Cache_withException() throws CacheDataException {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        Mockito.when(cache.getValue(key)).thenReturn(person.getId());

        Person person1 = secondaryCache.get(key, (id) -> {
            throw new RuntimeException("异常");
        }, () -> new IdObject<>(person.getId(), person));

        Assert.assertSame(person, person1);
    }

    @Test
    public void testGet_Cache_withGetException() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        try {
            Mockito.when(cache.getValue(key)).thenThrow(new CacheDataException());
        } catch (CacheDataException e) {
            e.printStackTrace();
        }

        Person person1 = secondaryCache.get(key, (id) -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(4);
            person3.setName("huxuan2");
            return person3;
        }, () -> new IdObject<>(person.getId(), person));

        Assert.assertSame(person, person1);
    }

    @Test
    public void testGet_Cache_withSaveException() {
        final Person person = new Person();
        person.setId(1);
        person.setMemberId(3);
        person.setName("huxuan");

        String key = makeKey();
        try {
            Mockito.doThrow(new CacheDataException()).when(cache).setValue(key, person.getId());
        } catch (CacheDataException e) {
            e.printStackTrace();
        }

        Person person1 = secondaryCache.get(key, (id) -> {
            Person person3 = new Person();
            person3.setId(2);
            person3.setMemberId(4);
            person3.setName("huxuan2");
            return person3;
        }, () -> new IdObject<>(person.getId(), person));

        Assert.assertSame(person, person1);

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
            return new IdObject<Person>(person3.getId(), person3);
        });

        Assert.assertNotSame(person, person2);

        Assert.assertEquals(person.getId(), person2.getId());
        Assert.assertEquals(person.getMemberId(), person2.getMemberId());
        Assert.assertEquals(person.getName(), person2.getName());
    }
}
