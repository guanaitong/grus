/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;


import com.ciicgat.grus.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import com.ciicgat.sdk.lang.url.UrlCoder;
import com.ciicgat.sdk.redis.config.RedisSetting;
import com.ciicgat.sdk.redis.config.SpringRedisConfCreator;
import com.ciicgat.sdk.util.system.WorkRegion;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by August.Zhou on 2017/3/29 11:02.
 */
public class SessionManager {
    @VisibleForTesting
    static final String SESSION_ID_COOKIE_NAME = "GSESSIONID";
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
    private static final String SESSION_ID_TAG = "___SESSION_ID_TAG____";
    private static final SessionIdGenerator SESSIONIDGENERATOR = new SessionIdGenerator();


    private volatile static RedisConnectionFactory redisConnectionFactory;
    private static boolean secure = WorkRegion.getCurrentWorkRegion().isProduct();
    private static FrequencyController frequencyController = new DefaultFrequencyController();

    private static RedisConnectionFactory getRedisConnectionFactory() {
        if (redisConnectionFactory == null) {
            synchronized (SessionManager.class) {
                if (redisConnectionFactory == null) {
                    RedisSetting redisSetting = GlobalGconfConfig.getConfig().getBean("session-redis-config.json", RedisSetting.class);
                    redisConnectionFactory = SpringRedisConfCreator.newRedisConnectionFactory(redisSetting, true);
                }
            }
        }
        return redisConnectionFactory;
    }

    public static void setFrequencyController(FrequencyController frequencyController) {
        SessionManager.frequencyController = frequencyController;
    }

    public static void setSecure(boolean secure) {
        SessionManager.secure = secure;
    }

    /**
     * 获取当前请求的session，如果没有会话或者之前会话已过期，会生成一个新的
     *
     * @return
     */
    public static HttpSession getCurrentSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return getCurrentSession(requestAttributes.getRequest(), requestAttributes.getResponse());
    }

    /**
     * 获取当前请求的session，如果没有会话或者之前会话已过期，会生成一个新的
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    public static HttpSession getCurrentSession(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        Object value = httpServletRequest.getAttribute(SESSION_ID_TAG);
        if (value == null) {
            value = getCurrentSession0(httpServletRequest, httpServletResponse, true).setServletContext(httpServletRequest.getServletContext());
            httpServletRequest.setAttribute(SESSION_ID_TAG, value);
        }
        return (HttpSession) value;
    }

    public static HttpSession getCurrentSession(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, boolean writeCookie) {
        Object value = httpServletRequest.getAttribute(SESSION_ID_TAG);
        if (value == null) {
            value = getCurrentSession0(httpServletRequest, httpServletResponse, writeCookie).setServletContext(httpServletRequest.getServletContext());
            httpServletRequest.setAttribute(SESSION_ID_TAG, value);
        }
        return (HttpSession) value;
    }

    public static HttpSession getSessionById(final String sessionId) {
        if (exists(sessionId)) {
            return new RedisSession(sessionId, false);
        }
        return null;
    }

    private static RedisSession getCurrentSession0(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, boolean writeCookie) {
        final String sessionId = getCookieValue(SESSION_ID_COOKIE_NAME, httpServletRequest);
        if (sessionId != null && exists(sessionId)) {
            return new RedisSession(sessionId, false);
        }

        //控制session产生的频次，防止DDOS攻击和爬虫
        if (frequencyController.isOverLimit(httpServletRequest, httpServletResponse)) {
            throw new RuntimeException("too many request");
        }

        final String newSessionId = SESSIONIDGENERATOR.generateSessionId();
        LOGGER.info("generate session,old {} ,new {}", sessionId, newSessionId);
        if (writeCookie) {
            Cookie cookie = new Cookie(SESSION_ID_COOKIE_NAME, newSessionId);
            cookie.setMaxAge(getSessionProp().getMaxSessionCookieAliveTime());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setSecure(secure); //true为只允许https
            httpServletResponse.addCookie(cookie);
        }
        return new RedisSession(newSessionId, true);
    }

    static SessionProp getSessionProp() {
        return GlobalGconfConfig.getConfig().getBean("session-prop.properties", SessionProp.class);
    }


    private static boolean exists(final String sessionId) {
        byte[] v = execute(redisConnection -> redisConnection.hGet(Bytes.toBytes(RedisSession.PREFIX + sessionId), RedisSession.CREATIONTIME_TAG));
        if (v == null || v.length == 0) {
            return false;
        }
        String value = Bytes.toString(v);
        try {
            return System.currentTimeMillis() - Long.parseLong(value) < getSessionProp().getMaxSessionAliveTime();
        } catch (NumberFormatException e) {
            LOGGER.error(value, e);
        }
        return false;
    }

    static String getCookieValue(String cookieName, HttpServletRequest httpServletRequest) {
        Objects.requireNonNull(cookieName);
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return UrlCoder.decode(cookie.getValue());
            }
        }
        return null;
    }

    static <T> T execute(Function<RedisConnection, T> callback) {
        try (RedisConnection connection = getRedisConnectionFactory().getConnection()) {
            return callback.apply(connection);
        }
    }
}
