/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;

import com.ciicgat.sdk.lang.exception.CacheDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by August.Zhou on 2017/1/4 14:37.
 */
@ExtendWith(MockitoExtension.class)
public class TestPrimaryCache {

    @Mock
    private LocalCache cache;


    private PrimaryCache primaryCache;

    @BeforeEach
    public void setup() {
        primaryCache = new PrimaryCache(cache);
    }


    @Test
    public void testGet_HitCache() throws CacheDataException {
        String key = "1";
        Person person = new Person();
        person.setId(1);
        person.setName("huxuan");
        Mockito.when(cache.getValue(key)).thenReturn(person);

        Person person1 = primaryCache.get(key, () -> {
            Person person2 = new Person();
            person2.setId(2);
            person2.setName("huxuan2");
            return person2;
        });

        Assertions.assertSame(person, person1);
    }


    @Test
    public void testGet_MissCache() throws CacheDataException {
        String key = "1";
        Mockito.when(cache.getValue(key)).thenReturn(null);
        final Person person = new Person();
        person.setId(1);
        person.setName("huxuan");
        Person person1 = primaryCache.get(key, () -> person);
        Assertions.assertSame(person, person1);
    }


    //    @Test(expected = RuntimeException.class)
    @Test
    public void testGet_MissCache_WithException() {
        String key = "1";
        Mockito.when(cache.getValue(key)).thenReturn(null);
        final Person person = new Person();
        person.setId(1);
        person.setName("huxuan");
        Assertions.assertThrows(RuntimeException.class, () -> primaryCache.get(key, () -> {
            throw new RuntimeException("db error");
        }));;
    }


    @Test
    public void testGet_WithCacheGetException() {
        String key = "1";

        try {
            Mockito.when(cache.getValue(key)).thenThrow(new CacheDataException());
        } catch (CacheDataException e) {
            e.printStackTrace();
        }
        final Person person = new Person();
        person.setId(1);
        person.setName("huxuan");

        Person person1 = primaryCache.get(key, () -> person);
        Assertions.assertSame(person, person1);
    }

    @Test
    public void testGet_WithCacheSaveException() {
        String key = "1";
        final Person person = new Person();
        person.setId(1);
        person.setName("huxuan");
        try {
            Mockito.doThrow(new CacheDataException()).when(cache).setValue(key, person);
        } catch (CacheDataException e) {
            e.printStackTrace();
        }
        Assertions.assertThrows(CacheDataException.class, () -> primaryCache.get(key, () -> person));
    }
}
