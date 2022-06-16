/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.mq.metrics.DelegateMetricsCollector;
import com.ciicgat.sdk.util.system.Systems;
import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by August.Zhou on 2019-11-06 14:01.
 */
public class CacheFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheFactory.class);

    private static Set<ExchangeCacheKey> exchangeCaches = new HashSet<>();
    private static ConcurrentMap<ConnectionCacheKey, Connection> cache = new ConcurrentHashMap<>();

    /**
     * 确保同一个exchange的同一个角色，全局只有一个实例。
     * 比如对于exchangeName=("app.exchange")的，它全局只能创建一个消费者、一个生产者
     *
     * @param exchange
     * @param consumer
     * @return
     */
    public static synchronized String checkExchangeExisted(String exchange, boolean consumer) {
        Objects.requireNonNull(exchange, "exchangeName为空");
        ExchangeCacheKey exchangeCacheKey = new ExchangeCacheKey(exchange, consumer);
        if (exchangeCaches.contains(exchangeCacheKey)) {
            throw new IllegalArgumentException(String.format("exchange %s for %s existed", exchange, consumer ? "consumer" : "producer"));
        } else {
            exchangeCaches.add(exchangeCacheKey);
        }
        return exchange;
    }

    public static Connection getConnection(AbstractBuilder builder, boolean consumer) {
        final ConnectionCacheKey connectionCacheKey = new ConnectionCacheKey(builder, consumer);
        Connection connection = cache.get(connectionCacheKey);
        if (connection != null) {
            return connection;
        }
        synchronized (CacheFactory.class) {
            connection = cache.get(connectionCacheKey);
            if (connection != null) {
                return connection;
            }
            try {
                connection = newConnection(builder, consumer);
            } catch (IOException e) {
                throw new RabbitMqException(e);
            } catch (TimeoutException e) {
                throw new RabbitMqException(e);
            }
            cache.put(connectionCacheKey, connection);
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
        Map<String, Object> defaultClientProperties = AMQConnection.defaultClientProperties();
        //连接上写上应用名字，方便排查
        defaultClientProperties.put("x-app-name", Systems.APP_NAME);
        defaultClientProperties.put("x-connection-role", consumer ? "consumer" : "producer");
        factory.setClientProperties(defaultClientProperties);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setMetricsCollector(consumer ? DelegateMetricsCollector.getConsumerInstance() : DelegateMetricsCollector.getProducerInstance());

        //这边只是new了，只要不调用execute(Runnable command)方法就不会创建新线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Threads.newThreadFactory("Grus-MQ", Thread.NORM_PRIORITY, false));
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

    private static class ExchangeCacheKey {

        private final String exchange;
        private final boolean consumer;

        ExchangeCacheKey(String exchange, boolean consumer) {
            this.exchange = exchange;
            this.consumer = consumer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExchangeCacheKey that = (ExchangeCacheKey) o;
            return consumer == that.consumer && Objects.equals(exchange, that.exchange);
        }

        @Override
        public int hashCode() {
            return Objects.hash(exchange, consumer);
        }
    }

    private static class ConnectionCacheKey {
        private final String host;
        private final String user;
        private final String pwd;
        private final int port;
        private final String vhost;
        private final boolean consumer;

        ConnectionCacheKey(AbstractBuilder builder, boolean consumer) {
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
            if (!(o instanceof ConnectionCacheKey)) return false;
            ConnectionCacheKey connectionCacheKey = (ConnectionCacheKey) o;
            return port == connectionCacheKey.port && consumer == connectionCacheKey.consumer && Objects.equals(host, connectionCacheKey.host) && Objects.equals(user, connectionCacheKey.user) && Objects.equals(pwd, connectionCacheKey.pwd) && Objects.equals(vhost, connectionCacheKey.vhost);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, user, pwd, port, vhost, consumer);
        }
    }
}
