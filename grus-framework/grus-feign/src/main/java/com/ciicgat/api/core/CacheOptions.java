/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ApiCache;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by August.Zhou on 2019-03-06 17:23.
 */
public class CacheOptions implements ApiCache {

    private String method;

    /**
     * 参数的序列号，按顺序组装缓存key，不传将以方法名做key
     * 确保参数非空时toString不会给出"null"结果
     */
    private int[] params = {};

    /**
     * 缓存过期时间
     */
    private long expireSeconds = 300;

    /**
     * 最大缓存大小
     */
    private long maxCacheSize = 10240L;

    /**
     * 并发级别
     */
    private int concurrencyLevel = 16;


    /**
     * 当值为null时，是否缓存，默认为true
     *
     * @return
     */
    private boolean cacheNullValue = true;


    public String getMethod() {
        return method;
    }

    public CacheOptions setMethod(String method) {
        this.method = method;
        return this;
    }

    public CacheOptions setParams(int[] params) {
        this.params = params;
        return this;
    }

    public CacheOptions setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
        return this;
    }

    public CacheOptions setMaxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }

    public CacheOptions setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public CacheOptions setCacheNullValue(boolean cacheNullValue) {
        this.cacheNullValue = cacheNullValue;
        return this;
    }

    @Override
    public int[] params() {
        return params;
    }

    @Override
    public long expireSeconds() {
        return expireSeconds;
    }

    @Override
    public long maxCacheSize() {
        return maxCacheSize;
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevel;
    }

    @Override
    public boolean cacheNullValue() {
        return cacheNullValue;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CacheOptions.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheOptions)) return false;
        CacheOptions that = (CacheOptions) o;
        return expireSeconds == that.expireSeconds &&
                maxCacheSize == that.maxCacheSize &&
                concurrencyLevel == that.concurrencyLevel &&
                cacheNullValue == that.cacheNullValue &&
                Objects.equals(method, that.method) &&
                Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method, expireSeconds, maxCacheSize, concurrencyLevel, cacheNullValue);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }
}
