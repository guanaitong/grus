/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.grus.sentinel.RuleOptions;
import feign.Request;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    private List<String> ignoreErrorMethods;

    /**
     * 类级别的限流降级规则
     */
    private Set<RuleOptions<?>> classRules;

    /**
     * 方法级别的限流规则
     */
    private Map<String, Set<RuleOptions<?>>> methodRules;

    public ServiceCacheKey(Class<?> serviceClazz, CacheOptions cacheOptions, Request.Options reqoptions, boolean logReq, boolean logResp, List<String> ignoreErrorMethods,
                           Set<RuleOptions<?>> classRules, Map<String, Set<RuleOptions<?>>> methodRules) {
        this.serviceClazz = serviceClazz;
        this.cacheOptions = cacheOptions;
        if (reqoptions != null) {
            this.connectTimeoutMillis = reqoptions.connectTimeoutMillis();
            this.readTimeoutMillis = reqoptions.readTimeoutMillis();
        }
        this.logReq = logReq;
        this.logResp = logResp;
        this.ignoreErrorMethods = ignoreErrorMethods;
        this.classRules = classRules;
        this.methodRules = methodRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceCacheKey that = (ServiceCacheKey) o;
        return connectTimeoutMillis == that.connectTimeoutMillis && readTimeoutMillis == that.readTimeoutMillis && logReq == that.logReq && logResp == that.logResp && Objects.equals(serviceClazz, that.serviceClazz) && Objects.equals(cacheOptions, that.cacheOptions) && Objects.equals(ignoreErrorMethods, that.ignoreErrorMethods) && Objects.equals(classRules, that.classRules) && Objects.equals(methodRules, that.methodRules);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(serviceClazz);
        result = 31 * result + Objects.hashCode(cacheOptions);
        result = 31 * result + connectTimeoutMillis;
        result = 31 * result + readTimeoutMillis;
        result = 31 * result + Boolean.hashCode(logReq);
        result = 31 * result + Boolean.hashCode(logResp);
        result = 31 * result + Objects.hashCode(ignoreErrorMethods);
        result = 31 * result + Objects.hashCode(classRules);
        result = 31 * result + Objects.hashCode(methodRules);
        return result;
    }
}
