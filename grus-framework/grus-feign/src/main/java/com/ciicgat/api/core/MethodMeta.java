/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ApiCache;
import com.ciicgat.api.core.annotation.IgnoreError;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.google.common.base.Defaults;
import feign.InvocationHandlerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2022/10/20 14:05
 */
class MethodMeta {
    private final InvocationHandlerFactory.MethodHandler methodHandler;
    private final ApiCache apiCache;
    private final AtomicInteger current = new AtomicInteger();

    private final boolean isDefault;

    private final boolean logReq;

    private final boolean logResp;

    private final boolean ignoreError;

    private final Method method;

    private final Object defaultReturnValue;
    private volatile Cache<String, Object> cache = null;

    MethodMeta(Method method, InvocationHandlerFactory.MethodHandler methodHandler, ApiCache apiCache, boolean logReq, boolean logResp, boolean ignoreErrorInclude) {
        this.method = method;
        this.methodHandler = methodHandler;
        this.apiCache = apiCache;
        this.isDefault = method.isDefault();
        this.ignoreError = ignoreErrorInclude || isIgnoreError(method);
        this.logReq = logReq;
        this.logResp = logResp;
        this.defaultReturnValue = defaultReturnValue(method);
    }

    private static boolean isIgnoreError(Method method) {
        return method.getAnnotation(IgnoreError.class) != null ||
                method.getDeclaringClass().getAnnotation(IgnoreError.class) != null ||
                method.getDeclaringClass().getPackage().getAnnotation(IgnoreError.class) != null;
    }

    private static Object defaultReturnValue(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType.isPrimitive()) {
            return Defaults.defaultValue(returnType);
        } else if (Optional.class.equals(returnType)) {
            return Optional.empty();
        } else if (Collection.class.isAssignableFrom(returnType)) {
            if (ArrayList.class.equals(returnType)) {
                return new ArrayList<>(1);
            } else if (Vector.class.equals(returnType)) {
                return new Vector<>(1);
            } else if (LinkedList.class.equals(returnType)) {
                return new LinkedList<>();
            } else if (HashSet.class.equals(returnType)) {
                return new HashSet<>(1);
            } else if (TreeSet.class.equals(returnType)) {
                return new TreeSet<>();
            } else if (List.class.isAssignableFrom(returnType)) {
                return new ArrayList<>(1);
            } else if (Set.class.isAssignableFrom(returnType)) {
                return new HashSet<>(1);
            }
        } else if (Map.class.isAssignableFrom(returnType)) {
            if (HashMap.class.equals(returnType)) {
                return new HashMap<>(2);
            } else if (LinkedHashMap.class.equals(returnType)) {
                return new LinkedHashMap<>(2);
            } else if (TreeMap.class.equals(returnType)) {
                return new TreeMap<>();
            } else if (Hashtable.class.equals(returnType)) {
                return new Hashtable<>(2);
            } else if (IdentityHashMap.class.equals(returnType)) {
                return new IdentityHashMap<>();
            }
        }
//        else if (Primitives.isWrapperType(returnType)) {
//            return Defaults.defaultValue(Primitives.unwrap(returnType));
//        }
        return null;
    }

    public InvocationHandlerFactory.MethodHandler getMethodHandler() {
        return methodHandler;
    }

    public ApiCache getApiCache() {
        return apiCache;
    }

    public int incrementAndGet() {
        return current.incrementAndGet();
    }

    public int decrementAndGet() {
        return current.decrementAndGet();
    }

    public boolean isIgnoreError() {
        return ignoreError;
    }

    public Object getDefaultReturnValue() {
        return defaultReturnValue;
    }

    public Cache<String, Object> getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = Caffeine.newBuilder()
                            .expireAfterWrite(apiCache.expireSeconds(), TimeUnit.SECONDS)
                            .maximumSize(apiCache.maxCacheSize())
                            .scheduler(Scheduler.systemScheduler())
                            .initialCapacity(16)
                            .build();
                }
            }
        }
        return cache;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isLogReq() {
        return logReq;
    }

    public boolean isLogResp() {
        return logResp;
    }

    public Method getMethod() {
        return method;
    }
}
