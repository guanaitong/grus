/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import com.ciicgat.api.core.annotation.ApiSecurity;
import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.sdk.util.system.Systems;

import java.lang.reflect.Method;

/**
 * Created by August.Zhou on 2023/10/31 14:56
 */
public class Services {

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


    public static boolean isSecure(Class<?> serviceClazz) {
        if (Systems.IN_K8S) {
            ApiSecurity annotation = serviceClazz.getAnnotation(ApiSecurity.class);
            if (annotation != null) {
                return true;
            }
            annotation = serviceClazz.getPackage().getAnnotation(ApiSecurity.class);
            return annotation != null;
        }
        return false;
    }

    public static LogExclude getLogExclude(Method method) {
        LogExclude logExclude = method.getAnnotation(LogExclude.class);
        if (logExclude != null) {
            return logExclude;
        }
        return method.getDeclaringClass().getAnnotation(LogExclude.class);
    }
}
