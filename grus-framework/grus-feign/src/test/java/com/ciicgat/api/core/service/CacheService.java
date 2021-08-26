/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ApiCache;
import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.TestBean;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Created by August.Zhou on 2018-10-19 13:24.
 */
@ServiceName("cache")
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface CacheService {

    @RequestLine("POST /getBean")
    @ApiCache(expireSeconds = 30)
    TestBean getBean();

    @RequestLine("POST /getBeanWithTwoParams")
    @ApiCache(params = {0, 1}, expireSeconds = 30)
    TestBean getBeanWithTwoParams(@Param("i") int i, @Param("j") int j);


    @RequestLine("POST /getBeanNotCacheNullValue")
    @ApiCache(expireSeconds = 30, cacheNullValue = false)
    TestBean getBeanNotCacheNullValue();

}
