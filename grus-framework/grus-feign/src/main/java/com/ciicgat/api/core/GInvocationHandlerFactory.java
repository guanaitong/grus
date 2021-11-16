/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ApiCache;
import com.ciicgat.api.core.annotation.IgnoreError;
import com.ciicgat.api.core.interceptor.FallbackInterceptor;
import com.ciicgat.api.core.interceptor.GHandlerInterceptor;
import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.grus.logger.LogUtil;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeConfig;
import com.ciicgat.grus.service.GrusService;
import com.ciicgat.grus.service.GrusServiceStatus;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Defaults;
import feign.InvocationHandlerFactory;
import feign.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static feign.Util.checkNotNull;

/**
 * java方法调用的代理工厂及代理类
 *
 * @author Wei Jiaju
 * @date Created in 2018/6/19
 */
@VisibleForTesting
class GInvocationHandlerFactory implements InvocationHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(GInvocationHandlerFactory.class);

    private static Object NULL = new Object();

    private final String serviceName;
    private final GrusService grusService;
    private final GrusServiceStatus grusServiceStatus;
    private final CacheOptions cacheOptions;
    private final FallbackFactory<?> fallbackFactory;

    private GrusRuntimeConfig grusRuntimeConfig = GrusFramework.getGrusRuntimeManager().getGrusRuntimeConfig();

    private final boolean logReq;

    private final boolean logResp;

    private final boolean enableSentinel;


    GInvocationHandlerFactory(GrusServiceStatus grusServiceStatus,
                              CacheOptions cacheOptions,
                              FallbackFactory<?> fallbackFactory,
                              boolean logReq,
                              boolean logResp,
                              boolean enableSentinel) {
        this.serviceName = Objects.requireNonNull(grusServiceStatus.getGrusService().getServiceName());
        this.cacheOptions = cacheOptions;
        this.grusServiceStatus = grusServiceStatus;
        this.grusService = grusServiceStatus.getGrusService();
        this.fallbackFactory = fallbackFactory;
        this.logReq = logReq;
        this.logResp = logResp;
        this.enableSentinel = enableSentinel;
    }

    static Map<Method, Method> toFallbackMethod(Map<Method, MethodHandler> dispatch) {
        Map<Method, Method> result = new LinkedHashMap<>(dispatch.size());
        for (Method method : dispatch.keySet()) {
            method.setAccessible(true);
            result.put(method, method);
        }
        return result;
    }

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return new GFeignInvocationHandler(target, dispatch);
    }

    private boolean isIgnoreError(Method method) {
        return method.getAnnotation(IgnoreError.class) != null ||
                method.getDeclaringClass().getAnnotation(IgnoreError.class) != null ||
                method.getDeclaringClass().getPackage().getAnnotation(IgnoreError.class) != null;
    }

    private Object defaultValue(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType != null && returnType.isPrimitive()) {
            return Defaults.defaultValue(returnType);
        } else if (Optional.class.equals(returnType)) {
            return Optional.empty();
        }
        return null;
    }


    private class GFeignInvocationHandler implements InvocationHandler {


        private final Target target;
        private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;
        private ConcurrentHashMap<Method, Cache<String, Object>> cacheMap;
        private final Map<Method, Method> fallbackMethodMap;
        private final GHandlerInterceptor[] gHandlerInterceptors;

        GFeignInvocationHandler(Target target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
            this.target = checkNotNull(target, "target");
            this.dispatch = checkNotNull(dispatch, "dispatch for %s", target);
            this.fallbackMethodMap = toFallbackMethod(dispatch);
            this.gHandlerInterceptors = new GHandlerInterceptor[]{new FallbackInterceptor()};
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return Boolean.FALSE;
                }
            } else if ("hashCode".equals(method.getName())) {
                return super.hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            }
            if (method.isDefault()) {
                return dispatch.get(method).invoke(args);
            }

            LogExclude logExclude = method.getAnnotation(LogExclude.class);
            if (logReq && LogUtil.checkPrintReq(logExclude)) {
                LOGGER.info("FEIGN_REQ METHOD: {} PARAM: {}", getPrintMethod(method), args == null ? "" : LogUtil.truncate(JSON.toJSONString(args)));
            }
            Object resp = invoke0(proxy, method, args);
            if (logResp && LogUtil.checkPrintResp(logExclude)) {
                LOGGER.info("FEIGN_RSP METHOD: {} RESULT: {}", getPrintMethod(method), resp == null ? "" : LogUtil.truncate(JSON.toJSONString(resp)));
            }
            return resp;
        }

        private Object invoke0(Object proxy, Method method, Object[] args) throws Throwable {
            final ApiCache apiCache = getApiCache(method);

            try {
                if (apiCache != null) {
                    grusServiceStatus.incrementSucceeded();
                    return invokeWithCache(apiCache, method, args);
                } else {
                    for (GHandlerInterceptor interceptor : gHandlerInterceptors) {
                        interceptor.preHandle(proxy, method, args, serviceName);
                    }

                    grusServiceStatus.incrementSucceeded();
                    return dispatch.get(method).invoke(args);
                }
            } catch (Throwable throwable) {
                if (!Objects.equals(throwable.getClass(), BusinessFeignException.class)) {
                    grusServiceStatus.incrementFailed();
                    Alert.send("Feign调用异常,type:" + target.type() + ",method:" + method.getName(), throwable);
                }
                if (isIgnoreError(method)) {
                    LOGGER.warn("Exception has been ignored", throwable);
                    return defaultValue(method);
                }
                if (fallbackFactory == null) {
                    throw throwable;

                }
                Object fallback = fallbackFactory.create(throwable);
                return fallbackMethodMap.get(method).invoke(fallback, args);
            }
        }


        private ApiCache getApiCache(Method method) {
            if (cacheOptions == null) {
                return method.getAnnotation(ApiCache.class);
            }
            if (method.getName().equals(cacheOptions.getMethod())) {
                return cacheOptions;
            }
            return null;
        }


        /**
         * 处理本地缓存
         *
         * @param apiCache apiCache
         * @param method   method
         * @param args     args
         * @return 实际对象
         * @throws Throwable
         */
        private Object invokeWithCache(ApiCache apiCache, Method method, Object[] args) throws Throwable {
            final Cache<String, Object> cache = getCache(apiCache, method);

            //拼接缓存key
            final StringBuilder sb = new StringBuilder(method.getName());
            for (int seq : apiCache.params()) {
                sb.append(args[seq]);
                sb.append("__");
            }
            final String cacheKey = sb.toString();

            //获取缓存，如空则加载
            Object cacheValue = cache.getIfPresent(cacheKey);
            if (cacheValue != null) {
                return cacheValue == NULL ? null : cacheValue;
            }

            Object result = null;
            try {
                result = dispatch.get(method).invoke(args);
            } catch (Throwable throwable) {
                //当ApiCache和IgnoreError同时标记，遇到异常时，ApiCache会生效。
                //否则，只有ApiCache标记，遇到异常时，ApiCache不会生效
                if (isIgnoreError(method)) {
                    result = defaultValue(method);
                } else {
                    throw throwable;
                }
            }

            //GUAVA CACHE的value是不能为null的，不然会抛异常
            if (result != null) {
                cache.put(cacheKey, result);
            } else if (apiCache.cacheNullValue()) {
                cache.put(cacheKey, NULL);
            }
            return result;
        }


        private Cache<String, Object> getCache(ApiCache apiCache, Method method) {
            if (cacheMap == null) {
                cacheMap = new ConcurrentHashMap<>();
            }
            Cache<String, Object> cache = cacheMap.get(method);
            if (cache == null) {
                Cache<String, Object> newCache = Caffeine.newBuilder()
                        .expireAfterWrite(apiCache.expireSeconds(), TimeUnit.SECONDS)
                        .maximumSize(apiCache.maxCacheSize())
                        .scheduler(Scheduler.systemScheduler())
                        .initialCapacity(16)
                        .build();
                cache = cacheMap.putIfAbsent(method, newCache);
                if (cache == null) {
                    cache = newCache;
                }
            }
            return cache;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof GFeignInvocationHandler) {
                GFeignInvocationHandler other = (GFeignInvocationHandler) obj;
                return target.equals(other.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return target.toString();
        }
    }

    private static String getPrintMethod(Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }
}

