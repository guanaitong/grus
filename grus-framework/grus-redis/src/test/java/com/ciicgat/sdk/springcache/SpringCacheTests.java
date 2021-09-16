/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.threads.Threads;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by August.Zhou on 2018-11-15 13:19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CacheApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringCacheTests {
    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CacheService cacheService;
    @Resource
    private CacheManager cacheManager2;
    @Resource
    private CacheManager cacheManager3;
    @Resource
    private Cache frequencyAsyncCache;

    @Before
    public void setUp() throws Exception {
        String url = String.format("http://127.0.0.1:%d/", port);
        System.out.println(String.format("port is : [%d]", port));
        this.base = new URL(url);
    }

    /**
     * 默认缓存管理器（redis）单测
     */
    @Test
    public void test1() {
        String randomText = String.valueOf(Math.random());
        // 第一次访问,miss cache
        ResponseEntity<String> response1 = this.restTemplate.getForEntity(
                this.base.toString() + "/uidCache1?uid=" + randomText, String.class, "");
        assertResp(randomText, response1);


        // 第二次访问,miss cache
        ResponseEntity<String> response2 = this.restTemplate.getForEntity(
                this.base.toString() + "/uidCache1?uid=" + randomText, String.class, "");
        assertResp(randomText, response2);

        // 第三次访问,evict cache
        ResponseEntity<String> response3 = this.restTemplate.getForEntity(
                this.base.toString() + "/evictCache1?uid=" + randomText, String.class, "");
        assertResp(randomText, response3);

        // 第四次访问,miss cache
        ResponseEntity<String> response4 = this.restTemplate.getForEntity(
                this.base.toString() + "/uidCache1?uid=" + randomText, String.class, "");
        assertResp(randomText, response4);
    }

    private void assertResp(String randomText, ResponseEntity<String> responseEntity) {
        Assert.assertEquals(randomText, JSON.parse(responseEntity.getBody(), new TypeReference<ApiResponse<String>>() {
        }).getData());
    }


    /**
     * 开启二级缓存缓存管理器（redis+caffeine）单测
     */
    @Test
    public void test2() {
        String randomText = String.valueOf(Math.random());
        // 第一次访问,miss cache
        ResponseEntity<String> response1 = this.restTemplate.getForEntity(
                this.base.toString() + "/uidCache2?uid=" + randomText, String.class, "");
        assertResp(randomText, response1);


        // 第二次访问,miss cache
        ResponseEntity<String> response2 = this.restTemplate.getForEntity(
                this.base.toString() + "/uidCache2?uid=" + randomText, String.class, "");
        assertResp(randomText, response2);

        // 第三次访问,evict cache
        ResponseEntity<String> response3 = this.restTemplate.getForEntity(
                this.base.toString() + "/evictCache2?uid=" + randomText, String.class, "");
        assertResp(randomText, response3);

        // 第四次访问,miss cache
        ResponseEntity<String> response4 = this.restTemplate.getForEntity(
                this.base.toString() + "/uidCache2?uid=" + randomText, String.class, "");
        assertResp(randomText, response4);
// for debug test
//        Threads.sleepSeconds(1000);
    }

    @Test
    public void test_l2cache() {
        String uid = UUID.randomUUID().toString();
        // 第一次访问，走目标方法，Miss cache
        String first = cacheService.get(uid, 1);

        // 第二次访问，from local cache
        String second = cacheService.get(uid, 2);
        Assert.assertSame(first, second);
        Assert.assertTrue(second.startsWith("1"));

        // 第三次访问，evict local cache, from redis cache
        Cache cache = cacheManager2.getCache("caffeineRedisCache");
        Optional.ofNullable(cache).ifPresent(Cache::clear);
        String third = cacheService.get(uid, 3);
        Assert.assertNotSame(second, third);
        Assert.assertEquals(second, third);

        // 第四次访问，evict cache，new cache
        cacheService.update(uid);
        String fourth = cacheService.get(uid, 4);
        Assert.assertTrue(fourth.startsWith("4"));

        // 第五次访问，from new cache
        String fifth = cacheService.get(uid, 5);
        Assert.assertSame(fourth, fifth);
        Assert.assertTrue(fifth.startsWith("4"));
    }

    @Test
    public void test_mixCache() {
        String uid = UUID.randomUUID().toString();
        // default, use l2cache
        // 第一次访问，走目标方法，Miss cache
        String first = cacheService.getFromDefault(uid, 1);

        // 第二次访问，from local cache
        String second = cacheService.getFromDefault(uid, 2);
        Assert.assertSame(first, second);
        Assert.assertTrue(second.startsWith("1"));

        // use redis cache
        // 第一次访问，走目标方法，Miss cache
        String firstFromUseRedisCache = cacheService.getFromUseRedisCache(uid, 1);

        // 第二次访问，from redis cache
        String secondFromUseRedisCache = cacheService.getFromUseRedisCache(uid, 2);
        Assert.assertNotSame(firstFromUseRedisCache, secondFromUseRedisCache);
        Assert.assertEquals(firstFromUseRedisCache, secondFromUseRedisCache);

        // use local cache serialize
        // 第一次访问，走目标方法，Miss cache
        String firstFromUseLocalCacheSerialize = cacheService.getFromUseLocalCacheSerialize(uid, 1);

        // 第二次访问，from local cache serialize
        String secondFromUseLocalCacheSerialize = cacheService.getFromUseLocalCacheSerialize(uid, 2);
        Assert.assertNotSame(firstFromUseLocalCacheSerialize, secondFromUseLocalCacheSerialize);
        Assert.assertEquals(firstFromUseLocalCacheSerialize, secondFromUseLocalCacheSerialize);

        // use local cache
        // 第一次访问，走目标方法，Miss cache
        String firstFromUseLocalCache = cacheService.getFromUseLocalCache(uid, 1);

        // 第二次访问，from local cache
        String secondFromUseLocalCache = cacheService.getFromUseLocalCache(uid, 2);
        Assert.assertSame(firstFromUseLocalCache, secondFromUseLocalCache);
        Assert.assertTrue(second.startsWith("1"));

        // 第三次访问，evict local cache, from redis cache
        Cache cache = cacheManager3.getCache("useLocalCache");
        Optional.ofNullable(cache).ifPresent(Cache::clear);
        String thirdFromUseLocalCache = cacheService.getFromUseLocalCache(uid, 3);
        Assert.assertNotSame(secondFromUseLocalCache, thirdFromUseLocalCache);
        Assert.assertTrue(thirdFromUseLocalCache.startsWith("3"));

        // use Local Cache No Expire
        // 第一次访问，走目标方法，Miss cache
        String firstFromUseLocalCacheNoExpire = cacheService.getFromUseLocalCacheNoExpire(uid, 1);

        // 第二次访问，from local cache
        String secondFromUseLocalCacheNoExpire = cacheService.getFromUseLocalCacheNoExpire(uid, 2);
        Assert.assertSame(firstFromUseLocalCacheNoExpire, secondFromUseLocalCacheNoExpire);
        Assert.assertTrue(second.startsWith("1"));
    }

    @Test
    public void test_null() {
        String uid = "testCache";
        String first = cacheService.get(uid, 1);
        Assert.assertNull(first);

        String nullVal = cacheService.get2(uid, 0);
        Assert.assertNull(nullVal);

        uid = UUID.randomUUID().toString();
        String second = cacheService.get(uid, 2);
        Assert.assertNotNull(second);

        uid = "testNull";
        String third = cacheService.getFromUseLocalCache(uid, 3);
        Assert.assertNull(third);

        String fourth = cacheService.getFromUseLocalCache2(uid, 4);
        Assert.assertNull(fourth);

        uid = UUID.randomUUID().toString();
        String fifth = cacheService.getFromUseLocalCache2(uid, 5);
        Assert.assertNotNull(fifth);
        Assert.assertTrue(fifth.startsWith("5"));
    }

    @Test
    public void test_autoRefresh() {
        String uid = UUID.randomUUID().toString();
        // default, use l2cache
        // 第一次访问，走目标方法，Miss cache
        String first = cacheService.getUseFrequencyAsyncCacheRefresher(uid, 1);

//        Threads.sleepSeconds(10);
        // 第二次访问，from local cache,trigger async refresh,but ignored refresh
        String second = cacheService.getUseFrequencyAsyncCacheRefresher(uid, 2);
        Assert.assertEquals(first, second);
        Assert.assertTrue(second.startsWith("1"));

        // wait frequency interval time
        Threads.sleepSeconds(3);
        // 第三次访问，from refreshed local cache,but trigger async refresh
        String third = cacheService.getUseFrequencyAsyncCacheRefresher(uid, 3);
        Assert.assertEquals(second, third);
        Assert.assertTrue(third.startsWith("1"));

        // get new value of previous refresh
        String four = cacheService.getUseFrequencyAsyncCacheRefresher(uid, 4);
//        Assert.assertEquals(four, third);
        Assert.assertTrue(four.startsWith("3"));


        // cacheKey test
        String firstUseCacheKey = cacheService.getUseCacheKey(() -> "customCacheKey", uid, 1);
        String secondUseCacheKey = frequencyAsyncCache.get("customCacheKey", () -> "customCacheKey");
        Assert.assertSame(firstUseCacheKey, secondUseCacheKey);
        Assert.assertTrue(secondUseCacheKey.startsWith("1"));

        // use redis cache
        // 第一次访问，走目标方法。Miss cache
        String firstFromUseRedisCache = cacheService.getUseNearExpiredAsyncCacheRefresher(uid, 1);

        // 第二次访问，from redis cache
        String secondFromUseRedisCache = cacheService.getUseNearExpiredAsyncCacheRefresher(uid, 2);
        Assert.assertNotSame(firstFromUseRedisCache, secondFromUseRedisCache);
        Assert.assertEquals(firstFromUseRedisCache, secondFromUseRedisCache);
        Assert.assertTrue(secondFromUseRedisCache.startsWith("1"));

        // 模拟等待异步刷新完成
        Threads.sleepSeconds(1);

        // 第三次访问，from refreshed redis cache
        String thirdFromUseRedisCache = cacheService.getUseNearExpiredAsyncCacheRefresher(uid, 3);
        Assert.assertNotEquals(firstFromUseRedisCache, thirdFromUseRedisCache);
        Assert.assertTrue(thirdFromUseRedisCache.startsWith("2"));
    }
}
