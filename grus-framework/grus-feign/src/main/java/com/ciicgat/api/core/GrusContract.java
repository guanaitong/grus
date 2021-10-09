/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ApiTimeout;
import feign.Contract;
import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.ciicgat.api.core.FeignHttpClient.CONNECT_TIMEOUT_TAG;
import static com.ciicgat.api.core.FeignHttpClient.READ_TIMEOUT_TAG;

/**
 * Created by August.Zhou on 2019-03-06 17:28.
 */
class GrusContract extends Contract.Default {

    @Override
    protected MethodMetadata parseAndValidateMetadata(Class<?> targetType, Method method) {
        MethodMetadata methodMetadata = super.parseAndValidateMetadata(targetType, method);
        ApiTimeout apiTimeout = null;
        for (Annotation methodAnnotation : method.getAnnotations()) {
            Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
            if (annotationType == ApiTimeout.class) {
                apiTimeout = (ApiTimeout) methodAnnotation;
                break;
            }
        }

        if (apiTimeout == null) {
            apiTimeout = method.getDeclaringClass().getAnnotation(ApiTimeout.class);
            if (apiTimeout == null) {
                apiTimeout = method.getDeclaringClass().getPackage().getAnnotation(ApiTimeout.class);
            }
        }

        if (apiTimeout != null) {
            methodMetadata.template().header(CONNECT_TIMEOUT_TAG, String.valueOf(apiTimeout.connectTimeoutMillis()));
            methodMetadata.template().header(READ_TIMEOUT_TAG, String.valueOf(apiTimeout.readTimeoutMillis()));
        }

        return methodMetadata;
    }


}

