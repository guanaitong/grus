/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import com.ciicgat.api.core.annotation.ServiceName;

/**
 * Created by August.Zhou on 2023/10/31 14:56
 */
public class ServiceNames {

    public static ServiceName getServiceName(Class<?> serviceType) {
        ServiceName serviceEndPoint = serviceType.getAnnotation(ServiceName.class);
        if (serviceEndPoint != null) {
            return serviceEndPoint;
        }
        serviceEndPoint = serviceType.getPackage().getAnnotation(ServiceName.class);
        if (serviceEndPoint != null) {
            return serviceEndPoint;
        }

        throw new RuntimeException("cannot find the appName");
    }
}
