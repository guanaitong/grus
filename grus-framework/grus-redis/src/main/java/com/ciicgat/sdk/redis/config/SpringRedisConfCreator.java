/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.config;

import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.grus.gconf.PublicKeyOwner;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .socketOptions(SocketOptions.builder().connectTimeout(Duration.ofSeconds(1L)).build())
                .build();
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions);
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
        String password = redisSetting.getPassword();
        if (StringUtils.isNotEmpty(redisSetting.getEncryptedPassword())) {
            password = PublicKeyOwner.decrypt(redisSetting.getEncryptedPassword());
        }
        if (redisSetting.getType() == 0) {
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
        } else if (redisSetting.getType() == 1) {
            RedisSentinelConfiguration redisConfiguration = new RedisSentinelConfiguration();
            RedisSetting.SentinelConfig sentinel = redisSetting.getSentinel();
            redisConfiguration.master(sentinel.getMaster());
            redisConfiguration.setSentinels(createSentinels(sentinel.getNodes()));
            redisConfiguration.setPassword(password);
            redisConfiguration.setDatabase(redisSetting.getDb());
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
