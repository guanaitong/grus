/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.redis.config.RedisSetting;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.ciicgat.sdk.redis.config.RedisConstants.DEFAULT_MAX_ATTEMPTS;

/**
 * @author August
 * @date 2020/3/6 4:58 PM
 */
public class SpringRedisConfCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRedisConfCreator.class);

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

    public static RedisConnectionFactory newRedisConnectionFactory(RedisSetting redisSetting, boolean init) {
        return newRedisConnectionFactory(redisSetting, init, ClientOptions.DisconnectedBehavior.DEFAULT);
    }

    /**
     * 如果其交给Spring管理，那么因为lettuceConnectionFactory继承了InitializingBean，
     * 无须大家手动调用afterPropertiesSet()方法,容器会自动调用，即init为false。<br/>
     * 如果不交给spring管理，那么传入的init需要为true
     *
     * @param redisSetting         gconf 上 Redis 配置对象
     * @param init                 是否不交给 spring 管理
     * @param disconnectedBehavior 如果设置REJECT_COMMANDS，那么当连接断开时，如果来了新的命令，那会拒绝。符合failfast要求。如果设置default会出现默认500ms的commandtimeout
     * @return RedisConnectionFactory
     */
    public static RedisConnectionFactory newRedisConnectionFactory(RedisSetting redisSetting, boolean init, ClientOptions.DisconnectedBehavior disconnectedBehavior) {
        RedisConfiguration redisConfiguration = newRedisConfiguration(redisSetting);
        ClientOptions.Builder clientOptionBuilder;
        if (redisSetting.getType() == RedisSetting.TYPE_CLUSTER) {
            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    .dynamicRefreshSources(true)
                    //开启自适应刷新
                    //.enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                    .enableAllAdaptiveRefreshTriggers() //开启所有自适应刷新，MOVED，ASK，PERSISTENT都会触发
                    .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30)) // 自适应刷新超时时间(默认30秒)
                    .enablePeriodicRefresh(Duration.ofSeconds(30))  // 开周期刷新
                    .build();
            ClusterClientOptions.Builder clusterClientOptionBuilder = ClusterClientOptions.builder();
            clusterClientOptionBuilder.topologyRefreshOptions(topologyRefreshOptions);
            clientOptionBuilder = clusterClientOptionBuilder;
        } else {
            clientOptionBuilder = ClientOptions.builder();
        }
        clientOptionBuilder.timeoutOptions(TimeoutOptions.enabled())
                .disconnectedBehavior(disconnectedBehavior)
                .autoReconnect(true) //lettuce会启动后台线程，检测连接状态以及尝试重连接
                .socketOptions(SocketOptions.builder().keepAlive(true).connectTimeout(Duration.ofMillis(redisSetting.getConnectTimeoutMillis())).build());
        ClientOptions clientOptions = clientOptionBuilder.build();

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
            LOGGER.warn("!!!YOU ARE USE STANDALONE REDIS,THIS WILL BE FORBIDDEN A FEW MONTHS LATER!!!");
            LOGGER.warn("!!!您正在使用单点的redis，几个月后会禁止该行为，请及时迁移!!!");
            RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
            RedisSetting.StandaloneConfig standalone = redisSetting.getStandalone();
            redisConfiguration.setHostName(standalone.getHost());
            redisConfiguration.setPort(standalone.getPort());
            redisConfiguration.setPassword(password);
            redisConfiguration.setDatabase(redisSetting.getDb());
            return redisConfiguration;
        } else if (redisSetting.getType() == RedisSetting.TYPE_SENTINEL) {
            RedisSentinelConfiguration redisConfiguration = new RedisSentinelConfiguration();
            RedisSetting.SentinelConfig sentinel = redisSetting.getSentinel();
            redisConfiguration.master(sentinel.getMaster());
            List<RedisNode> nodes = new ArrayList<>();
            for (String node : convertNodes(sentinel.getNodes())) {
                String[] parts = node.split(":");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            }
            redisConfiguration.setSentinels(nodes);
            redisConfiguration.setPassword(password);
            redisConfiguration.setDatabase(redisSetting.getDb());
            return redisConfiguration;
        } else if (redisSetting.getType() == RedisSetting.TYPE_CLUSTER) {
            RedisSetting.ClusterConfig cluster = redisSetting.getCluster();
            RedisClusterConfiguration redisConfiguration = new RedisClusterConfiguration(convertNodes(cluster.getNodes()));
            redisConfiguration.setMaxRedirects(DEFAULT_MAX_ATTEMPTS);
            redisConfiguration.setPassword(password);
            return redisConfiguration;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static List<String> convertNodes(String nodesText) {
        List<String> nodes = new ArrayList<>();
        for (String node : nodesText.split(",")) {
            String[] parts = node.split(":");
            if (parts.length != 2) {
                throw new IllegalStateException("Invalid nodesText '" + node + "'");
            }
            nodes.add(node);
        }
        return nodes;
    }

    private static void setPool(RedisProperties.Pool pool, RedisSetting redisSetting) {
        pool.setEnabled(true);
        pool.setMaxActive(redisSetting.getMaxTotal());
        pool.setMaxIdle(redisSetting.getMaxIdle());
        pool.setMinIdle(redisSetting.getMinIdle());
    }

    /**
     * 改方法，将redisSetting配置，转换为spring RedisProperties的配置。
     *
     * @param redisSetting
     * @return
     */
    public static RedisProperties newRedisProperties(RedisSetting redisSetting) {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setClientName(RedisSetting.clientName());
        String password = redisSetting.getFinalPassword();
        redisProperties.setPassword(password);
        redisProperties.setConnectTimeout(Duration.ofMillis(redisSetting.getConnectTimeoutMillis()));
        redisProperties.setTimeout(Duration.ofMillis(redisSetting.getTimeoutMillis()));
        setPool(redisProperties.getLettuce().getPool(), redisSetting);
        setPool(redisProperties.getJedis().getPool(), redisSetting);
        if (redisSetting.getType() == RedisSetting.TYPE_STANDALONE) {
            LOGGER.warn("!!!YOU ARE USE STANDALONE REDIS,THIS WILL BE FORBIDDEN A FEW MONTHS LATER!!!");
            LOGGER.warn("!!!您正在使用单点的redis，几个月后会禁止该行为，请及时迁移!!!");
            redisProperties.setDatabase(redisSetting.getDb());
            RedisSetting.StandaloneConfig standalone = redisSetting.getStandalone();
            redisProperties.setHost(standalone.getHost());
            redisProperties.setPort(standalone.getPort());
        } else if (redisSetting.getType() == RedisSetting.TYPE_SENTINEL) {
            redisProperties.setDatabase(redisSetting.getDb());
            RedisSetting.SentinelConfig sentinel = redisSetting.getSentinel();
            RedisProperties.Sentinel rSentinel = new RedisProperties.Sentinel();
            redisProperties.setSentinel(rSentinel);
            rSentinel.setMaster(sentinel.getMaster());
            rSentinel.setNodes(convertNodes(sentinel.getNodes()));
        } else if (redisSetting.getType() == RedisSetting.TYPE_CLUSTER) {
            redisProperties.setDatabase(0);
            RedisSetting.ClusterConfig cluster = redisSetting.getCluster();
            RedisProperties.Cluster rCluster = new RedisProperties.Cluster();
            redisProperties.setCluster(rCluster);
            rCluster.setMaxRedirects(DEFAULT_MAX_ATTEMPTS);
            rCluster.setNodes(convertNodes(cluster.getNodes()));
            RedisProperties.Lettuce.Cluster.Refresh refresh = redisProperties.getLettuce().getCluster().getRefresh();
            refresh.setDynamicRefreshSources(true);
            refresh.setAdaptive(true);
            refresh.setPeriod(Duration.ofSeconds(60));
        } else {
            throw new UnsupportedOperationException();
        }

        return redisProperties;
    }
}
