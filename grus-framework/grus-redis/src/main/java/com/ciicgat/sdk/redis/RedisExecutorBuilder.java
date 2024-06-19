/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.sdk.redis.config.RedisSetting;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Connection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.util.Pool;

import java.util.HashSet;
import java.util.Set;


/**
 * @author August.Zhou
 * @date 2019-04-25 18:12
 */
public class RedisExecutorBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExecutorBuilder.class);

    private final RedisSetting setting;

    public RedisExecutorBuilder(RedisSetting setting) {
        this.setting = setting;
    }

    public RedisExecutor newRedisExecutor() {
        if (setting.getType() == RedisSetting.TYPE_CLUSTER) {
            return new JedisClusterRedisExecutor(this.setting, newJedisCluster());
        }
        return new JedisPoolRedisExecutor(this.setting, newPool());
    }

    public JedisCluster newJedisCluster() {
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        String nodes = setting.getCluster().getNodes();
        for (String node : nodes.split(",")) {
            jedisClusterNode.add(HostAndPort.from(node));
        }
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        setting.configPool(poolConfig);
        return new JedisCluster(jedisClusterNode,
                setting.getConnectTimeoutMillis(), setting.getTimeoutMillis(), 5, setting.getFinalPassword(), poolConfig);
    }

    public Pool<Jedis> newPool() {
        if (setting.getType() == RedisSetting.TYPE_STANDALONE) {
            StandaloneChecker.check(setting);
            return newJedisPool();
        } else if (setting.getType() == RedisSetting.TYPE_SENTINEL) {
            return newJedisSentinelPool();
        }
        throw new UnsupportedOperationException();
    }


    public final JedisPool newJedisPool() {
        RedisSetting.StandaloneConfig standalone = setting.getStandalone();
        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        return new JedisPool(jedisPoolConfig,
                standalone.getHost(),
                standalone.getPort(),
                setting.getConnectTimeoutMillis(),
                setting.getTimeoutMillis(),
                setting.getFinalPassword(),
                this.setting.getDb(),
                RedisSetting.clientName()
        ) {

            @Override
            public Jedis getResource() {
                try {
                    return super.getResource();
                } catch (RuntimeException e) {
                    //默认情况的错误是不输出redis配置信息的，这里重载一下
                    LOGGER.error(setting.toString(), e);
                    Alert.send("redis pool error:" + setting, e);
                    throw e;
                }
            }

        };
    }

    public final JedisSentinelPool newJedisSentinelPool() {
        RedisSetting.SentinelConfig sentinelConfig = setting.getSentinel();
        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        return new JedisSentinelPool(
                sentinelConfig.getMaster(),
                Set.of(sentinelConfig.getNodes().split(",")),
                jedisPoolConfig,
                setting.getConnectTimeoutMillis(),
                setting.getTimeoutMillis(),
                setting.getFinalPassword(),
                this.setting.getDb(),
                RedisSetting.clientName()
        ) {

            @Override
            public Jedis getResource() {
                try {
                    return super.getResource();
                } catch (RuntimeException e) {
                    //默认情况的错误是不输出redis配置信息的，这里重载一下
                    LOGGER.error(setting.toString(), e);
                    Alert.send("redis pool error:" + setting, e);
                    throw e;
                }
            }

        };
    }


    private JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        setting.configPool(jedisPoolConfig);
        return jedisPoolConfig;
    }

}
