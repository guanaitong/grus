/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.util.system.Systems;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.impl.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by August.Zhou on 2019-11-06 14:01.
 */
public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);

    private static ConcurrentMap<ConnectionCacheKey, Connection> cache = new ConcurrentHashMap<>();

    public static Connection newConnectionFromCache(String addresses, String user, String pwd, String vhost, int parallel) {
        final ConnectionCacheKey connectionCacheKey = new ConnectionCacheKey(addresses, user, pwd, vhost);
        Connection connection = cache.get(connectionCacheKey);
        if (connection != null) {
            return connection;
        }
        synchronized (ConnectionFactory.class) {
            connection = cache.get(connectionCacheKey);
            if (connection != null) {
                return connection;
            }
            connection = newConnection(addresses, user, pwd, vhost, parallel);

            cache.put(connectionCacheKey, connection);
        }
        return connection;
    }


    public static Connection newConnection(String addresses, String user, String pwd, String vhost, int parallel) {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        //初始化工厂
        factory.setUsername(user);
        factory.setPassword(pwd);
        factory.setVirtualHost(vhost);
        //10s心跳
        factory.setRequestedHeartbeat(10);
        Map<String, Object> defaultClientProperties = AMQConnection.defaultClientProperties();
        //连接上写上应用名字，方便排查
        defaultClientProperties.put("x-app-name", Systems.APP_NAME);
        factory.setClientProperties(defaultClientProperties);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setExceptionHandler(new AlertExceptionHandler());

        //这边只是new了，只要不调用execute(Runnable command)方法就不会创建新线程池
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(parallel, parallel, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Threads.newThreadFactory("Grus-MQ", Thread.NORM_PRIORITY, false));
        Connection connection = null;
        try {
            connection = factory.newConnection(threadPoolExecutor, Address.parseAddresses(addresses));
        } catch (IOException e) {
            throw new RabbitMqException(e);
        } catch (TimeoutException e) {
            throw new RabbitMqException(e);
        }
        connection.addShutdownListener(cause -> LOGGER.error("[rabbitMQ 连接被关闭],close:{}", cause.getLocalizedMessage(), cause));
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


    private static class ConnectionCacheKey {
        private final String addresses;
        private final String user;
        private final String pwd;
        private final String vhost;

        ConnectionCacheKey(String addresses, String user, String pwd, String vhost) {
            this.addresses = addresses;
            this.user = user;
            this.pwd = pwd;
            this.vhost = vhost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConnectionCacheKey that = (ConnectionCacheKey) o;
            return Objects.equals(addresses, that.addresses) && Objects.equals(user, that.user) && Objects.equals(pwd, that.pwd) && Objects.equals(vhost, that.vhost);
        }

        @Override
        public int hashCode() {
            return Objects.hash(addresses, user, pwd, vhost);
        }
    }
}
