/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.GConfBeanValidator;
import com.ciicgat.grus.gconf.GlobalGconfConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: August
 * @Date: 2021/7/12 16:12
 */
public class KubernetesClientConfig implements GConfBeanValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesClientConfig.class);
    static KubernetesClientConfig DEFAULT = new KubernetesClientConfig();

    private long connectTimeout = 200;
    private int retryCount = 3;
    private List<KubernetesAppConfig> apps = new ArrayList<>();
    private KubernetesAppConfig defaultConfig = new KubernetesAppConfig();
    @JsonIgnore
    private Map<String, KubernetesAppConfig> cache = new HashMap<>();
    private List<RetryCondition> retryConditions = RetryCondition.DEFAULTS;

    public static KubernetesClientConfig getConfig() {
        KubernetesClientConfig config = GlobalGconfConfig.getConfig().getBean("k8s-service-client.json", content -> {
            try {
                KubernetesClientConfig res = JSON.parse(content, new TypeReference<>() {
                });
                return res;
            } catch (Exception e) {
                LOGGER.warn(content, e);
                return DEFAULT;
            }
        });
        return config == null ? DEFAULT : config;
    }

    public KubernetesAppConfig getAppConfig(String name) {
        return cache.getOrDefault(name, defaultConfig);
    }

    public boolean couldRetry(Throwable e) {
        for (RetryCondition retryCondition : retryConditions) {
            if (e.getClass().getName().equals(retryCondition.getClassName()) && e.getMessage().contains(retryCondition.getMessage())) {
                return true;
            }
        }
        return false;
    }

    public long getConnectTimeout() {
        return this.connectTimeout;
    }

    public List<RetryCondition> getRetryConditions() {
        return retryConditions;
    }

    public void setRetryConditions(List<RetryCondition> retryConditions) {
        this.retryConditions = retryConditions;
    }

    public void setConnectTimeout(final long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(final int retryCount) {
        this.retryCount = retryCount;
    }

    public List<KubernetesAppConfig> getApps() {
        return this.apps;
    }

    public void setApps(final List<KubernetesAppConfig> apps) {
        this.apps = apps;
    }

    public KubernetesAppConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public void setDefaultConfig(final KubernetesAppConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public void validate() throws RuntimeException {
        for (KubernetesAppConfig app : this.apps) {
            this.cache.put(app.getName(), app);
        }
        if (this.getDefaultConfig() == null) {
            this.setDefaultConfig(new KubernetesAppConfig());
        }
    }
}
