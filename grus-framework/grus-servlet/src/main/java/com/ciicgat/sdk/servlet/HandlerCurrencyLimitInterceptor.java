/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    private final int max;

    public HandlerCurrencyLimitInterceptor(int max) {
        this.max = max;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();

        AtomicInteger atomicInteger = concurrentMap.get(method);
        if (atomicInteger == null) {
            AtomicInteger newAtomicInteger = new AtomicInteger();
            atomicInteger = concurrentMap.putIfAbsent(method, newAtomicInteger);
            if (atomicInteger == null) {
                atomicInteger = newAtomicInteger;
            }
        }
        if (atomicInteger.incrementAndGet() >= max) {
            LOGGER.error("方法 " + method + " 响应过慢，超过最大并发数上限，为了保护其他接口，直接拒绝该方法后续请求！！！");
            atomicInteger.decrementAndGet();
            response.getWriter().print(JSON.toJSONString(ApiResponse.fail(ErrorCode.REQUEST_BLOCK)));
            response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
            return false;
        }
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        AtomicInteger atomicInteger = concurrentMap.get(method);
        if (atomicInteger == null) {
            return;
        }
        atomicInteger.decrementAndGet();
    }
}
