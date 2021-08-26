/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.util.system.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.ciicgat.sdk.session.SessionManager.execute;
import static com.ciicgat.sdk.session.SessionManager.getSessionProp;

/**
 * Created by August.Zhou on 2017/3/28 19:11.
 */
@SuppressWarnings("deprecation")
class RedisSession implements HttpSession {
    static final String PREFIX = "GSESSION_" + Systems.APP_NAME + "_";
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSession.class);
    static byte[] CREATIONTIME_TAG = Bytes.toBytes("__CREATION_TIME__TAG__");
    private static RedisSerializer<Object> SERIALIZER = RedisSerializer.java();
    protected volatile long creationTime = -1L;
    protected volatile int maxInactiveInterval = -10;
    private String sessionId;
    private ServletContext servletContext;
    private String redisKey;
    private byte[] redisKeyBytes;
    private boolean isNew;

    RedisSession(String sessionId, boolean isNew) {
        this.sessionId = sessionId;
        this.redisKey = PREFIX + sessionId;
        this.redisKeyBytes = Bytes.toBytes(this.redisKey);
        this.isNew = isNew;
        final SessionProp sessionProp = getSessionProp();
        if (this.isNew) {
            creationTime = System.currentTimeMillis();
            execute(redisConnection -> {
                redisConnection.hSet(redisKeyBytes, CREATIONTIME_TAG, Bytes.toBytes(String.valueOf(creationTime)));
                redisConnection.expire(redisKeyBytes, sessionProp.getDefaultSessionAliveTime());
                return null;
            });
        } else {
            try {
                execute(redisConnection -> redisConnection.expire(redisKeyBytes, sessionProp.getDefaultSessionAliveTime()));
            } catch (Exception e) {
                LOGGER.error("unexpected error", e);
            }
        }
    }


    @Override
    public long getCreationTime() {
        if (!this.isNew && creationTime == -1) {
            byte[] value = execute(redisConnection -> redisConnection.hGet(redisKeyBytes, CREATIONTIME_TAG));
            creationTime = (value == null || value.length == 0) ? 0 : Long.parseLong(Bytes.toString(value));
        }
        return creationTime;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    public RedisSession setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        return this;
    }

    @Override
    public int getMaxInactiveInterval() {
        if (maxInactiveInterval == -10) {
            Long ttl = execute(redisConnection -> redisConnection.ttl(redisKeyBytes));
            if (ttl.intValue() == -1) {
                maxInactiveInterval = -1;
            } else if (ttl.intValue() == -2) {
                maxInactiveInterval = -1;
            } else {
                maxInactiveInterval = ttl.intValue();
            }

        }
        return maxInactiveInterval;
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        this.maxInactiveInterval = interval;
        execute(redisConnection -> redisConnection.expire(redisKeyBytes, interval));
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(final String name) {
        byte[] redisValue = execute(redisConnection -> redisConnection.hGet(redisKeyBytes, Bytes.toBytes(name)));
        try {
            return SERIALIZER.deserialize(redisValue);
        } catch (Exception e) {
            LOGGER.error("redisValue: " + redisValue, e);
            return null;
        }
    }

    @Override
    public Object getValue(String name) {
        return this.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        final var res = execute(redisConnection -> redisConnection.hGetAll(redisKeyBytes));
        res.remove(CREATIONTIME_TAG);
        final Iterator<byte[]> keys = res.keySet().iterator();
        return new Enumeration<>() {
            @Override
            public boolean hasMoreElements() {
                return keys.hasNext();
            }

            @Override
            public String nextElement() {
                return Bytes.toString(keys.next());
            }

            @Override
            public String toString() {
                return res.keySet().toString();
            }
        };
    }

    @Override
    public String[] getValueNames() {
        final var res = execute(redisConnection -> redisConnection.hGetAll(redisKeyBytes));
        res.remove(CREATIONTIME_TAG);
        List<String> stringList = new ArrayList<>(res.size());
        for (byte[] bytes : res.keySet()) {
            stringList.add(Bytes.toString(bytes));
        }
        return stringList.toArray(new String[0]);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (!(value instanceof Serializable)) {
            throw new IllegalArgumentException("value must implements java.io.Serializable,value type: " + value.getClass());
        }
        final byte[] redisValue = SERIALIZER.serialize(value);
        final SessionProp sessionProp = getSessionProp();
        execute(redisConnection -> {
            redisConnection.hSet(redisKeyBytes, Bytes.toBytes(name), redisValue);
            redisConnection.expire(redisKeyBytes, sessionProp.getDefaultSessionAliveTime());
            return null;
        });
    }

    @Override
    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        execute(redisConnection -> redisConnection.hDel(redisKeyBytes, Bytes.toBytes(name)));
    }

    @Override
    public void removeValue(String name) {
        this.removeAttribute(name);
    }

    @Override
    public void invalidate() {
        execute(redisConnection -> redisConnection.del(redisKeyBytes));
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
