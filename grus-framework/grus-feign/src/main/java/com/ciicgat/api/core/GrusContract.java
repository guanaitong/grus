/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ApiTimeout;
import feign.Contract;
import feign.Headers;
import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.ciicgat.api.core.FeignHttpClient.CONNECT_TIMEOUT_TAG;
import static com.ciicgat.api.core.FeignHttpClient.READ_TIMEOUT_TAG;

/**
 * Created by August.Zhou on 2019-03-06 17:28.
 */
class GrusContract extends Contract.Default {
    private static final String HEADER_NAME = "Content-Type";

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        super.processAnnotationOnMethod(data, methodAnnotation, method);
        Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
        if (annotationType == Headers.class) {
            String[] headersOnMethod = Headers.class.cast(methodAnnotation).value();
            Map<String, Collection<String>> headerCollectionMap = toMap(headersOnMethod);
            if (headerCollectionMap.containsKey(HEADER_NAME)) {
                Map<String, Collection<String>> headers = data.template().headers();
                Collection<String> collection = headers.get(HEADER_NAME);
                if (collection != null && collection.size() > 1) {
                    //有多个Content-Type时，以最后一个为基准
                    data.template().headers(null); // to clear
                    Map<String, Collection<String>> newHeaders = new LinkedHashMap<>();
                    newHeaders.putAll(headers);
                    newHeaders.put(HEADER_NAME, headerCollectionMap.get(HEADER_NAME));
                    data.template().headers(newHeaders);
                }
            }

        }


    }

    private static Map<String, Collection<String>> toMap(String[] input) {
        Map<String, Collection<String>> result =
                new LinkedHashMap<>(input.length);
        for (String header : input) {
            int colon = header.indexOf(':');
            String name = header.substring(0, colon);
            if (!result.containsKey(name)) {
                result.put(name, new ArrayList<>(1));
            }
            result.get(name).add(header.substring(colon + 1).trim());
        }
        return result;
    }

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

