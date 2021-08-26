/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.kubernetes.KubernetesAppConfig;
import com.ciicgat.api.core.kubernetes.KubernetesClientConfig;
import com.ciicgat.api.core.kubernetes.KubernetesServiceDiscoveryClient;
import com.ciicgat.grus.service.discovery.ServiceDiscoveryClient;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: August
 * @Date: 2021/7/15 23:41
 */
public class ServiceDiscoveryClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscoveryClientUtil.class);

    private static volatile ServiceDiscoveryClient serviceDiscoveryClient;

    public static void tryInitClient(String name) {
        KubernetesClientConfig config = KubernetesClientConfig.getConfig();
        KubernetesAppConfig appConfig = config.getAppConfig(name);
        if (appConfig.isOn() && !WorkRegion.getCurrentWorkRegion().isPrepare()) {
            getServiceDiscoveryClient();
        }
    }

    public static ServiceDiscoveryClient getServiceDiscoveryClient() {
        if (serviceDiscoveryClient == null) {
            synchronized (ServiceDiscoveryClientUtil.class) {
                if (serviceDiscoveryClient == null) {
                    try {
                        serviceDiscoveryClient = new KubernetesServiceDiscoveryClient();
                    } catch (Exception e) {
                        LOGGER.error("init k8s error", e);
                        serviceDiscoveryClient = ServiceDiscoveryClient.NOOP;
                    }
                }
            }
        }
        return serviceDiscoveryClient;
    }

}
