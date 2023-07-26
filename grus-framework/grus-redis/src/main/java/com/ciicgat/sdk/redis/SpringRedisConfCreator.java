/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.redis.config.RedisSetting;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ciicgat.sdk.redis.config.RedisConstants.DEFAULT_MAX_ATTEMPTS;

/**
 * @author August
 * @date 2020/3/6 4:58 PM
 */
public class SpringRedisConfCreator {

    private SpringRedisConfCreator() {
    }

    public static RedisSetting getDefaultRedisSetting() {
        return RemoteConfigCollectionFactoryBuilder
                .getInstance()
                .getConfigCollection()
                .getBean("redis-config.json", RedisSetting.class);
    }

    public static RedisConnectionFactory newDefaultRedisConnectionFactory() {
        return newRedisConnectionFactory(getDefaultRedisSetting(), false);
    }

    public static RedisConnectionFactory newRedisConnectionFactory(RedisSetting redisSetting) {
        return newRedisConnectionFactory(redisSetting, false);
    }

    /**
     * 如果其交给Spring管理，那么因为lettuceConnectionFactory继承了InitializingBean，
     * 无须大家手动调用afterPropertiesSet()方法,容器会自动调用，即init为false。<br/>
     * 如果不交给spring管理，那么传入的init需要为true
     *
     * @param redisSetting gconf 上 Redis 配置对象
     * @param init         是否不交给 spring 管理
     * @return RedisConnectionFactory
     */
    public static RedisConnectionFactory newRedisConnectionFactory(RedisSetting redisSetting, boolean init) {
        RedisConfiguration redisConfiguration = newRedisConfiguration(redisSetting);
        ClientOptions clientOptions = ClientOptions.builder().timeoutOptions(TimeoutOptions.enabled())
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS) //当连接断开时，如果来了新的命令，那会拒绝。符合failfast要求。如果设置default会出现默认500ms的commandtimeout
                .autoReconnect(true) //lettuce会启动后台线程，检测连接状态以及尝试重连接
                .socketOptions(SocketOptions.builder().keepAlive(true).connectTimeout(Duration.ofMillis(redisSetting.getConnectTimeoutMillis())).build())
                .build();

        //这里默认使用了连接池。如果不适用连接池，lettuce默认是单连接的。
        //单连接可能会导致以下问题：
        //客户端通过1个连接分别发送2个请求，当数据分片2处于异常状态时，请求2（GET Key2）将返回超时，同时由于请求1（GET Key1）和请求2共用同一连接，导致请求1也无法正常返回。

        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
        redisSetting.configPool(config);

        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder().poolConfig(config)
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMillis(redisSetting.getTimeoutMillis()));
        String clientName = RedisSetting.clientName();
        if (org.springframework.util.StringUtils.hasText(clientName)) {
            builder.clientName(clientName);
        }
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactoryWrapper(redisConfiguration, builder.build(), redisSetting);
        if (init) {
            lettuceConnectionFactory.afterPropertiesSet();
        }
        return lettuceConnectionFactory;
    }

    private static RedisConfiguration newRedisConfiguration(RedisSetting redisSetting) {
        String password = redisSetting.getFinalPassword();
        if (redisSetting.getType() == RedisSetting.TYPE_STANDALONE) {
            RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
            RedisSetting.StandaloneConfig standalone = redisSetting.getStandalone();
            if (RedisSetting.isSuitableForOutOfK8sConfig(standalone)) {
                redisConfiguration.setHostName(standalone.getNodeHost());
                redisConfiguration.setPort(standalone.getNodePort());
            } else {
                redisConfiguration.setHostName(standalone.getHost());
                redisConfiguration.setPort(standalone.getPort());
            }
            redisConfiguration.setPassword(password);
            redisConfiguration.setDatabase(redisSetting.getDb());
            return redisConfiguration;
        } else if (redisSetting.getType() == RedisSetting.TYPE_SENTINEL) {
            RedisSentinelConfiguration redisConfiguration = new RedisSentinelConfiguration();
            RedisSetting.SentinelConfig sentinel = redisSetting.getSentinel();
            redisConfiguration.master(sentinel.getMaster());
            redisConfiguration.setSentinels(createSentinels(sentinel.getNodes()));
            redisConfiguration.setPassword(password);
            redisConfiguration.setDatabase(redisSetting.getDb());
            return redisConfiguration;
        } else if (redisSetting.getType() == RedisSetting.TYPE_CLUSTER) {
            RedisSetting.ClusterConfig cluster = redisSetting.getCluster();
            RedisClusterConfiguration redisConfiguration = new RedisClusterConfiguration(Arrays.asList(cluster.getNodes().split(",")));
            redisConfiguration.setMaxRedirects(DEFAULT_MAX_ATTEMPTS);
            redisConfiguration.setPassword(password);
            return redisConfiguration;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static List<RedisNode> createSentinels(String nodesText) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : nodesText.split(",")) {
            try {
                String[] parts = node.split(":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }
}
