/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkRegion;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by August.Zhou on 2018-11-21 14:54.
 */
public class K8sNamingService implements NamingService {
    @Override
    public String serviceLocation(String domain) {
        if (!Systems.IN_K8S) {
            return null;
        }

        WorkRegion workRegion = WorkRegion.getCurrentWorkRegion();
        String serviceDomainSuffix = ".".concat(workRegion.getServiceDomainSuffix());
        if (domain.lastIndexOf(serviceDomainSuffix) > -1) {
            String appName = domain.replace(serviceDomainSuffix, "");
            try {
                String k8sServiceHost = System.getenv(appName.toUpperCase().replaceAll("-", "_") + "_SERVICE_HOST");
                if (k8sServiceHost == null || k8sServiceHost.isEmpty()) {
                    return domain;
                }

                InetAddress[] value = InetAddress.getAllByName(appName);
                if (value == null || value.length == 0) {
                    return domain;
                }

                return appName;
            } catch (UnknownHostException e) {
                // no-ops
            }
        }

        return domain;
    }

    @Override
    public String resolve(String serviceName) {
        if (isK8sService(serviceName)) {
            return HTTP_PROTOCOL_PREFIX + serviceName;
        }
        return null;
    }

    /**
     * 判断一个服务名是否是k8s里的服务
     *
     * @param serviceName
     * @return
     */
    public static boolean isK8sService(String serviceName) {
        if (!Systems.IN_K8S) {
            return false;
        }
        try {
            String k8sServiceHost = System.getenv(serviceName.toUpperCase().replaceAll("-", "_") + "_SERVICE_HOST");
            if (k8sServiceHost == null || k8sServiceHost.isEmpty()) {
                return false;
            }
            InetAddress[] value = InetAddress.getAllByName(serviceName);
            if (value == null || value.length == 0) {
                return false;
            }

        } catch (UnknownHostException e) {
            return false;
        }
        return true;
    }

}
