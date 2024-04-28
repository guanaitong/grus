/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.sentinel;


import com.ciicgat.grus.sentinel.SentinelDefaultConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by August.Zhou on 2024/4/20 23:06
 */
@ConfigurationProperties(prefix = "grus.sentinel")
public class GrusSentinelProperties {
    /**
     * 客户端限流，所有的参数统计维度都是接口级别。所以设置的参数，都是针对的是单独某个接口。
     */
    private ClientRule client = new ClientRule();

    /**
     * 系统降级配置
     */
    private SystemRule system = new SystemRule();

    public SystemRule getSystem() {
        return system;
    }

    public void setSystem(SystemRule system) {
        this.system = system;
    }

    public ClientRule getClient() {
        return client;
    }

    public void setClient(ClientRule client) {
        this.client = client;
    }

    public static class ClientRule {
        /**
         * 降级规则
         */
        private SentinelDefaultConfig.Degrade degrade = new SentinelDefaultConfig.Degrade();
        /**
         * 限流规则
         */
        private SentinelDefaultConfig.Flow flow = new SentinelDefaultConfig.Flow();

        public SentinelDefaultConfig.Degrade getDegrade() {
            return degrade;
        }

        public void setDegrade(SentinelDefaultConfig.Degrade degrade) {
            this.degrade = degrade;
        }

        public SentinelDefaultConfig.Flow getFlow() {
            return flow;
        }

        public void setFlow(SentinelDefaultConfig.Flow flow) {
            this.flow = flow;
        }
    }


    public static class SystemRule {
        /**
         * 是否默认启用sentinel的系统规则
         */
        private boolean enabled = true;
        /**
         * tomcat默认的为200，我们设置降级的最大线程设置为180，在线程跑满之前，直接拒绝后续请求
         */
        private int maxThread = 180;

        /**
         * 降级的最大平均响应时间，单位毫秒
         */
        private int avgRt = -1;

        public int getMaxThread() {
            return maxThread;
        }

        public void setMaxThread(int maxThread) {
            this.maxThread = maxThread;
        }

        public int getAvgRt() {
            return avgRt;
        }

        public void setAvgRt(int avgRt) {
            this.avgRt = avgRt;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
