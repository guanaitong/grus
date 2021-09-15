/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.kubernetes.KubernetesClientConfig;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @Author: August
 * @Date: 2021/7/8 15:32
 */
public class ConfigRetryer extends Retryer.Default {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRetryer.class);

    public ConfigRetryer() {
        super(100, SECONDS.toMillis(1), 3);
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        final KubernetesClientConfig config = KubernetesClientConfig.getConfig();
        if (config.couldRetry(e.getCause())) {
            LOGGER.warn("retry");
            FrigateNotifier.sendMessageByAppName("feign retry for error: " + e.getMessage());
            super.continueOrPropagate(e);
        } else {
            throw e;
        }
    }

    @Override
    public Retryer clone() {
        return new ConfigRetryer();
    }
}
