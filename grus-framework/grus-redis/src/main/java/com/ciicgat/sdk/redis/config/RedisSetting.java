/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.config;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.PublicKeyOwner;
import com.ciicgat.sdk.redis.RedisExecutor;
import com.ciicgat.sdk.redis.RedisExecutorBuilder;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkIdc;
import com.ciicgat.sdk.util.system.WorkRegion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;


/**
 * gconf上标准的redis配置
 *
 * @author August
 * @date 2020/2/24 2:46 PM
 */
public class RedisSetting implements RedisConstants {
    public static final int TYPE_STANDALONE = 0;

    public static final int TYPE_SENTINEL = 1;

    public static final int TYPE_CLUSTER = 2;

    private String name;
    /**
     * redis类型，type=0时为单机模式，type=1时为哨兵模式，type=2时为集群模式
     */
    private int type;
    /**
     * 单机模式配置
     */
    private StandaloneConfig standalone;
    /**
     * 哨兵模式配置
     */
    private SentinelConfig sentinel;

    /**
     * 集群配置
     */
    private ClusterConfig cluster;
    /**
     * 明文密码，优先使用秘文密码，在秘文密码不存在时使用明文
     */
    private String password;
    /**
     * 秘文密码，优先使用秘文密码，在秘文密码不存在时使用明文
     */
    private String encryptedPassword;
    /**
     * 连接的redisDb
     */
    private int db;

    private Map<String, String> params = Collections.emptyMap();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public StandaloneConfig getStandalone() {
        return standalone;
    }

    public void setStandalone(StandaloneConfig standalone) {
        this.standalone = standalone;
    }

    public SentinelConfig getSentinel() {
        return sentinel;
    }

    public void setSentinel(SentinelConfig sentinel) {
        this.sentinel = sentinel;
    }

    public String getPassword() {
        return password;
    }

    /**
     * 如果有加密密码，优先把其解密内容作为 redis 的密码
     *
     * @return redis 链接的密码
     */
    @JsonIgnore
    public String getFinalPassword() {
        String finalPassword = password;
        if (StringUtils.isNotEmpty(this.getEncryptedPassword())) {
            finalPassword = PublicKeyOwner.decrypt(this.getEncryptedPassword());
        }
        return StringUtils.isEmpty(finalPassword) ? null : finalPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @JsonIgnore
    public int getParamsIntValue(String key, int defaultValue) {
        if (!params.containsKey(key)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(params.get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @JsonIgnore
    public int getConnectTimeoutMillis() {
        return getParamsIntValue("connectTimeout", DEFAULT_CONNECT_TIME_OUT_MILLIS);
    }

    @JsonIgnore
    public int getTimeoutMillis() {
        return getParamsIntValue("timeout", DEFAULT_TIME_OUT_MILLIS);
    }

    @JsonIgnore
    public int getMaxTotal() {
        return this.getParamsIntValue("maxTotal", DEFAULT_MAX_ACTIVE);
    }

    @JsonIgnore
    public int getMinIdle() {
        return this.getParamsIntValue("minIdle", DEFAULT_MIN_IDLE);
    }

    @JsonIgnore
    public int getMaxIdle() {
        return this.getParamsIntValue("maxIdle", DEFAULT_MAX_IDLE);
    }


    @JsonIgnore
    public void configPool(GenericObjectPoolConfig<?> poolConfig) {
        poolConfig.setMaxTotal(this.getMaxTotal());
        poolConfig.setMaxIdle(this.getMaxIdle());
        poolConfig.setMinIdle(this.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(this.getParamsIntValue("maxWaitMillis", DEFAULT_MAX_WAIT_MILLIS)));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClusterConfig getCluster() {
        return cluster;
    }

    public void setCluster(ClusterConfig cluster) {
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static class StandaloneConfig {
        /**
         * redis主机
         */
        private String host;
        /**
         * 端口
         */
        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

    }

    public static class SentinelConfig {
        /**
         * 需要redis集群名
         */
        private String master;
        /**
         * 哨兵节点列表
         */
        private String nodes;

        public String getMaster() {
            return master;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public String getNodes() {
            return nodes;
        }

        public void setNodes(String nodes) {
            this.nodes = nodes;
        }
    }

    public static class ClusterConfig {
        private String nodes;

        public String getNodes() {
            return nodes;
        }

        public void setNodes(String nodes) {
            this.nodes = nodes;
        }
    }

    public RedisExecutor newRedisExecutor() {
        return new RedisExecutorBuilder(this).newRedisExecutor();
    }

    // 主要用于trace追踪
    public String instanceName() {
        if (StringUtils.isNotEmpty(this.getName())) {
            return this.getName();
        } else if (this.getType() == RedisSetting.TYPE_STANDALONE) {
            return this.getStandalone().getHost() + ":" + this.getStandalone().getPort();
        } else if (this.getType() == RedisSetting.TYPE_SENTINEL) {
            return this.getSentinel().getMaster();
        } else if (this.getType() == RedisSetting.TYPE_CLUSTER) {
            return this.getCluster().getNodes();
        }
        return "redis";
    }


    public static String clientName() {
        return WorkRegion.getCurrentWorkRegion().getWorkIdc() == WorkIdc.ALI ? null : Systems.APP_NAME;
    }

}
