/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import feign.Request;

import java.util.Objects;

import static com.ciicgat.api.core.contants.TimeOutConstants.DEFAULT_CONNECT_TIMEOUT_MILLIS;
import static com.ciicgat.api.core.contants.TimeOutConstants.DEFAULT_READ_TIMEOUT_MILLIS;

/**
 * Created by August.Zhou on 2019-05-07 13:27.
 */
public class ServiceCacheKey {
    private Class<?> serviceClazz;

    private CacheOptions cacheOptions;

    private int connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
    private int readTimeoutMillis = DEFAULT_READ_TIMEOUT_MILLIS;

    private boolean logReq;

    private boolean logResp;

    public ServiceCacheKey(Class<?> serviceClazz, CacheOptions cacheOptions, Request.Options reqoptions, boolean logReq, boolean logResp) {
        this.serviceClazz = serviceClazz;
        this.cacheOptions = cacheOptions;
        if (reqoptions != null) {
            this.connectTimeoutMillis = reqoptions.connectTimeoutMillis();
            this.readTimeoutMillis = reqoptions.readTimeoutMillis();
        }
        this.logReq = logReq;
        this.logResp = logResp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceCacheKey)) return false;
        ServiceCacheKey that = (ServiceCacheKey) o;
        return connectTimeoutMillis == that.connectTimeoutMillis &&
                readTimeoutMillis == that.readTimeoutMillis &&
                logReq == that.logReq &&
                logResp == that.logResp &&
                Objects.equals(serviceClazz, that.serviceClazz) &&
                Objects.equals(cacheOptions, that.cacheOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceClazz, cacheOptions, connectTimeoutMillis, readTimeoutMillis, logReq, logResp);
    }
}
