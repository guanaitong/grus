/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.config;

import com.ciicgat.grus.alert.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-07-29 14:51
 */
class LettuceConnectionFactoryWrapper extends LettuceConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LettuceConnectionFactoryWrapper.class);

    private final RedisSetting redisSetting;

    LettuceConnectionFactoryWrapper(RedisConfiguration redisConfiguration, LettuceClientConfiguration clientConfig, RedisSetting redisSetting) {
        super(redisConfiguration, clientConfig);
        this.redisSetting = redisSetting;
    }

    @Override
    public RedisConnection getConnection() {
        RedisConnection connection = super.getConnection();
        if (connection instanceof LettuceConnection) {
            return new TracingRedisConnection(connection, redisSetting);
        } else {
            return connection;
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        try (RedisConnection connection = this.getConnection()) {
            connection.ping();
        } catch (Throwable e) {
            String msg = "redis connect error, please check redis-config.json in gconf";
            Alert.send(msg, e);
            LOGGER.error(msg, e);
            throw e;
        }
    }
}
