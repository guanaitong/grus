/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.performance.MaxConcurrency;
import com.ciicgat.grus.performance.SentinelProperties;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2022/10/5 12:58
 */
public class HandlerCurrencyLimitInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerCurrencyLimitInterceptor.class);
    private final ConcurrentMap<Method, AtomicInteger> concurrentMap = new ConcurrentHashMap<>();

    private final int globalMaxConcurrency = GlobalGconfConfig.getConfig().getBean("sentinel.properties", SentinelProperties.class).getWebMaxConcurrency();

    public HandlerCurrencyLimitInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        Method method = ((HandlerMethod) handler).getMethod();
        if (increaseReferece(request, method.toString(), 1) != 1) {
            return true;
        }
        AtomicInteger atomicInteger = concurrentMap.get(method);
        if (atomicInteger == null) {
            AtomicInteger newAtomicInteger = new AtomicInteger();
            atomicInteger = concurrentMap.putIfAbsent(method, newAtomicInteger);
            if (atomicInteger == null) {
                atomicInteger = newAtomicInteger;
            }
        }
        int maxConcurrency = globalMaxConcurrency;
        MaxConcurrency maxConcurrencyAnnotation = method.getAnnotation(MaxConcurrency.class);
        if (maxConcurrencyAnnotation != null) {
            maxConcurrency = maxConcurrencyAnnotation.value();
        }
        if (atomicInteger.incrementAndGet() > maxConcurrency) {
            LOGGER.error("{} 响应过慢，超过最大并发数上限，为了保护其他接口，直接拒绝该方法后续请求！！！", method);
            atomicInteger.decrementAndGet();
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        Method method = ((HandlerMethod) handler).getMethod();

        if (increaseReferece(request, method.toString(), -1) != 0) {
            return;
        }

        AtomicInteger atomicInteger = concurrentMap.get(method);
        if (atomicInteger == null) {
            return;
        }
        atomicInteger.decrementAndGet();
    }

    private static Integer increaseReferece(HttpServletRequest request, String rcKey, int step) {
        Object obj = request.getAttribute(rcKey);

        if (obj == null) {
            // initial
            obj = Integer.valueOf(0);
        }

        Integer newRc = (Integer) obj + step;
        request.setAttribute(rcKey, newRc);
        return newRc;
    }
}
