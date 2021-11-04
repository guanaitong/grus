/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.mq.metrics.DelegateMetricsCollector;
import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by August.Zhou on 2019-11-06 14:01.
 */
public class CacheConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheConnectionFactory.class);

    private static ConcurrentMap<CacheKey, Connection> cache = new ConcurrentHashMap<>();

    public static Connection getConnection(AbstractBuilder builder, boolean consumer) throws IOException, TimeoutException {
        final CacheKey cacheKey = new CacheKey(builder, consumer);
        Connection connection = cache.get(cacheKey);
        if (connection != null) {
            return connection;
        }
        synchronized (CacheConnectionFactory.class) {
            connection = cache.get(cacheKey);
            if (connection != null) {
                return connection;
            }
            connection = newConnection(builder, consumer);
            cache.put(cacheKey, connection);
        }
        return connection;
    }

    private static Connection newConnection(AbstractBuilder builder, boolean consumer) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //初始化工厂
        factory.setHost(builder.host);
        factory.setUsername(builder.user);
        factory.setPassword(builder.pwd);
        factory.setPort(builder.port);
        factory.setVirtualHost(builder.vhost);
        //10s心跳
        factory.setRequestedHeartbeat(10);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setMetricsCollector(consumer ? DelegateMetricsCollector.getConsumerInstance() : DelegateMetricsCollector.getProducerInstance());

        //这边只是new了，只要不调用execute(Runnable command)方法就不会创建新线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                4,
                4,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                Threads.newThreadFactory("Grus-MQ", Thread.NORM_PRIORITY, false));
        Connection connection = factory.newConnection(threadPoolExecutor);

        connection.addBlockedListener(new BlockedListener() {
            @Override
            public void handleBlocked(String reason) {
                LOGGER.warn(reason);
            }

            @Override
            public void handleUnblocked() {
                LOGGER.warn("handleUnblocked");
            }
        });

        return connection;
    }

    private static class CacheKey {
        private final String host;
        private final String user;
        private final String pwd;
        private final int port;
        private final String vhost;
        private final boolean consumer;

        CacheKey(AbstractBuilder builder, boolean consumer) {
            this.host = builder.host;
            this.user = builder.user;
            this.pwd = builder.pwd;
            this.port = builder.port;
            this.vhost = builder.vhost;
            this.consumer = consumer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheKey)) return false;
            CacheKey cacheKey = (CacheKey) o;
            return port == cacheKey.port &&
                    consumer == cacheKey.consumer &&
                    Objects.equals(host, cacheKey.host) &&
                    Objects.equals(user, cacheKey.user) &&
                    Objects.equals(pwd, cacheKey.pwd) &&
                    Objects.equals(vhost, cacheKey.vhost);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, user, pwd, port, vhost, consumer);
        }
    }
}
