/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.sdk.gconf.PublicKeyOwner;
import com.ciicgat.sdk.redis.config.RedisSetting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolAbstract;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Map;
import java.util.Set;

/**
 * @author August.Zhou
 * @date 2019-04-25 18:12
 */
public class RedisPoolBuilder implements RedisConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPoolBuilder.class);

    private final RedisSetting setting;

    public RedisPoolBuilder(RedisSetting setting) {
        this.setting = setting;
    }

    public RedisExecutor newRedisExecutor() {
        return new RedisExecutorImpl(this.setting, newPool());
    }

    public JedisPoolAbstract newPool() {
        if (setting.getType() == 0) {
            return newJedisPool();
        } else if (setting.getType() == 1) {
            return newJedisSentinelPool();
        }
        throw new UnsupportedOperationException();
    }


    public final JedisPool newJedisPool() {
        RedisSetting.StandaloneConfig standalone = setting.getStandalone();
        String host = standalone.getHost();
        int port = standalone.getPort();
        if (RedisSetting.isSuitableForOutOfK8sConfig(standalone)) {
            host = standalone.getNodeHost();
            port = standalone.getNodePort();
        }
        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        return new JedisPool(jedisPoolConfig,
                host,
                port,
                getIntValue("timeout", 2000),
                getPassword(),
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
                getIntValue("timeout", DEFAULT_TIME_OUT_MILLIS),
                getIntValue("timeout", DEFAULT_TIME_OUT_MILLIS),
                getPassword(),
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

    private int getIntValue(String key, int defaultValue) {
        Map<String, String> params = this.setting.getParams();
        if (!params.containsKey(key)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(params.get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(getIntValue("maxTotal", DEFAULT_MAX_ACTIVE));
        jedisPoolConfig.setMaxIdle(getIntValue("maxIdle", DEFAULT_MAX_IDLE));
        jedisPoolConfig.setMinIdle(getIntValue("minIdle", DEFAULT_MIN_IDLE));
        jedisPoolConfig.setMaxWaitMillis(getIntValue("maxWaitMillis", DEFAULT_MAX_WAIT_MILLIS));
        return jedisPoolConfig;
    }

    /**
     * 如果有加密密码，优先把其解密内容作为 redis 的密码
     *
     * @return redis 链接的密码
     */
    private String getPassword() {
        String password = this.setting.getPassword();
        if (StringUtils.isNotEmpty(this.setting.getEncryptedPassword())) {
            password = PublicKeyOwner.decrypt(this.setting.getEncryptedPassword());
        }
        return password;
    }

}
