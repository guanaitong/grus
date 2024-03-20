/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.config;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by August.Zhou on 2024/3/20 11:21
 */
public class ServiceConnectConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConnectConfig.class);

    static ServiceConnectConfig DEFAULT = new ServiceConnectConfig();
    private long connectTimeout = 200;
    private int retryCount = 3;

    private List<RetryCondition> retryConditions = RetryCondition.DEFAULTS;

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public List<RetryCondition> getRetryConditions() {
        return retryConditions;
    }

    public void setRetryConditions(List<RetryCondition> retryConditions) {
        this.retryConditions = retryConditions;
    }

    public boolean couldRetry(Throwable e) {
        for (RetryCondition retryCondition : retryConditions) {
            if (e.getClass().getName().equals(retryCondition.getClassName()) && e.getMessage().contains(retryCondition.getMessage())) {
                return true;
            }
        }
        return false;
    }

    public static ServiceConnectConfig getConfig() {
        ServiceConnectConfig config = GlobalGconfConfig.getConfig().getBean("service-connect.json", content -> {
            try {
                ServiceConnectConfig res = JSON.parse(content, new TypeReference<>() {
                });
                return res;
            } catch (Exception e) {
                LOGGER.warn(content, e);
                return DEFAULT;
            }
        });
        return config == null ? DEFAULT : config;
    }


}
