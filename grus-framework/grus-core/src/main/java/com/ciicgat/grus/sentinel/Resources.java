/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import java.lang.reflect.Method;

/**
 * @author August
 * @date 3/13/2024 1:30 PM
 */
public class Resources {

    public static String buildMethodResource(String serviceName, String classSimpleName, String method) {
        // 拼装资源
        StringBuilder resourceNameBuilder = new StringBuilder(serviceName)
                .append(".")
                .append(classSimpleName)
                .append(".")
                .append(method);
        return resourceNameBuilder.toString();
    }

    public static String buildMethodResource(String serviceName, Method method) {
        return buildMethodResource(serviceName, method.getDeclaringClass().getSimpleName(), method.getName());
    }

    public static String buildClassResource(String serviceName, String classSimpleName) {
        // 拼装资源
        StringBuilder resourceNameBuilder = new StringBuilder(serviceName)
                .append(".")
                .append(classSimpleName)
                .append(".*");
        return resourceNameBuilder.toString();
    }

    public static String buildAppServiceResource(String serviceName) {
        // 拼装资源
        StringBuilder resourceNameBuilder = new StringBuilder(serviceName)
                .append(".*");
        return resourceNameBuilder.toString();
    }
}
