/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.lang.convert.ApiResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by August.Zhou on 2018-11-15 13:15.
 */
@RestController
public class CacheController {


    @RequestMapping("/uidCache1")
    @Cacheable(cacheManager = "cacheManager1", value = "myCache", key = "#uid")
    public ApiResponse<String> uidCache1(String uid) {
        return ApiResponse.success(uid);
    }

    @RequestMapping("/evictCache1")
    @CacheEvict(cacheManager = "cacheManager1", value = "myCache", key = "#uid")
    public ApiResponse<String> evictCache1(String uid) {
        return ApiResponse.success(uid);
    }

    @RequestMapping("/uidCache2")
    @Cacheable(cacheManager = "cacheManager2", value = "myCache", key = "#uid")
    public ApiResponse<String> uidCache2(String uid) {
        return ApiResponse.success(uid);
    }

    @RequestMapping("/evictCache2")
    @CacheEvict(cacheManager = "cacheManager2", value = "myCache", key = "#uid")
    public ApiResponse<String> evictCache2(String uid) {
        return ApiResponse.success(uid);
    }
}
