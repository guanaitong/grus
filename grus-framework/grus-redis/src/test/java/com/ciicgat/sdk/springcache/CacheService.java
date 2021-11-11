/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wanchongyang
 * @date 2020/12/20 10:08 下午
 */
@Service
public class CacheService {
    @Resource
    private Cache frequencyAsyncCache;
    @Resource
    private Cache randomAsyncCache;

    @Cacheable(cacheManager = "cacheManager2", value = "caffeineRedisCache", key = "#uid")
    public String get(String uid, int num) {
        if ("testCache".equals(uid)) {
            return null;
        }

        return num + "_" + uid;
    }

    @Cacheable(cacheManager = "cacheManager2", value = "caffeineRedisCache", key = "#uid")
    public String get2(String uid, int num) {
        return num + "_" + uid;
    }

    @CacheEvict(cacheManager = "cacheManager2", value = "caffeineRedisCache", key = "#uid")
    public int update(String uid) {
        return 0;
    }

    @Cacheable(cacheManager = "cacheManager3", value = "useRedisCache", key = "#uid")
    public String getFromUseRedisCache(String uid, int num) {
        return getFromUseRedisCacheWithoutCache(uid, num);
    }

    public String getFromUseRedisCacheWithoutCache(String uid, int num) {
        return num + "_" + uid;
    }

    @Cacheable(cacheManager = "cacheManager3", value = "useLocalCacheSerialize", key = "#uid")
    public String getFromUseLocalCacheSerialize(String uid, int num) {
        return num + "_" + uid;
    }

    @Cacheable(cacheManager = "cacheManager3", value = "defaultMixCache", key = "#uid")
    public String getFromDefault(String uid, int num) {
        return getFromDefaultWithoutCache(uid, num);
    }

    public String getFromDefaultWithoutCache(String uid, int num) {
        return num + "_" + uid;
    }

    @Cacheable(cacheManager = "cacheManager3", value = "useLocalCache", key = "#uid")
    public String getFromUseLocalCache(String uid, int num) {
        if ("testNull".equals(uid)) {
            return null;
        }

        return num + "_" + uid;
    }

    @Cacheable(cacheManager = "cacheManager3", value = "useLocalCache", key = "#uid")
    public String getFromUseLocalCache2(String uid, int num) {
        return num + "_" + uid;
    }

    @Cacheable(cacheManager = "cacheManager3", value = "useLocalCacheNoExpire", key = "#uid")
    public String getFromUseLocalCacheNoExpire(String uid, int num) {
        return num + "_" + uid;
    }

    public String getUseFrequencyAsyncCacheRefresher(String uid, int num) {
        return frequencyAsyncCache.get(uid, () -> num + "_" + uid);
    }

    public String getRandomCacheRefresher(String uid, int num) {
        return randomAsyncCache.get(uid, () -> num + "_" + uid);
    }

    public String getUseCacheKey(CacheKey cacheKey, String uid, int num) {
        return frequencyAsyncCache.get(cacheKey, () -> num + "_" + uid);
    }
}
