/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

/**
 * gconf上标准的redis配置
 *
 * @author August
 * @date 2020/2/24 2:46 PM
 */
public class RedisSettingT {
    /**
     * redis类型，type=0时为单机模式，type=1时为哨兵模式
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

    public static class StandaloneConfig {
        /**
         * redis主机
         */
        private String host;
        /**
         * 端口
         */
        private int port;

        private String nodeHost;

        private int nodePort;

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

        public String getNodeHost() {
            return nodeHost;
        }

        public void setNodeHost(String nodeHost) {
            this.nodeHost = nodeHost;
        }

        public int getNodePort() {
            return nodePort;
        }

        public void setNodePort(int nodePort) {
            this.nodePort = nodePort;
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
}
