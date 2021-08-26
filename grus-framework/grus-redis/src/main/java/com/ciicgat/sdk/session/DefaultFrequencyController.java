/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by August.Zhou on 2017/7/7 12:56.
 */
class DefaultFrequencyController implements FrequencyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFrequencyController.class);

    private LoadingCache<String, AtomicLong> oneHourLimit = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(key -> new AtomicLong());


    private LoadingCache<String, AtomicLong> oneMinutesLimit = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(key -> new AtomicLong());

    private static boolean check(LoadingCache<String, AtomicLong> cache, String key, int maxCount) {
        try {
            return cache.get(key).incrementAndGet() > maxCount;
        } catch (Exception e) {
            LOGGER.error(key, e);
            return false;
        }
    }

    private static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    @Override
    public boolean isOverLimit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String ip = getRequestIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        SessionProp sessionProp = SessionManager.getSessionProp();
        return check(oneMinutesLimit, "total", sessionProp.getMaxCountOneMinutes())
                || check(oneHourLimit, ip, sessionProp.getIpMaxCountOneHour())
                || check(oneHourLimit, ip + userAgent, sessionProp.getIpUaMaxCountOneHour())
                || check(oneHourLimit, "total", sessionProp.getMaxCountOneHour());
    }
}
