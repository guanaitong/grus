/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.interceptor;

import com.ciicgat.api.core.BusinessFeignException;
import com.ciicgat.grus.fallback.FallbackConfig;
import com.ciicgat.grus.fallback.FallbackConstant;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.convert.ErrorCode;

import java.lang.reflect.Method;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/11/21 10:33
 * @Description:
 */
public class FallbackInterceptor implements GHandlerInterceptor {

    private ConfigCollection configCollection;

    public FallbackInterceptor() {
        try {
            configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection();
        } catch (Exception e) {
            configCollection = null;
        }
    }

    @Override
    public void preHandle(Object proxy, Method method, Object[] args, String serviceName) {
        if (configCollection == null) {
            return;
        }

        FallbackConfig fallbackConfig = configCollection.getBean(FallbackConstant.JSON_FILE_NAME, content -> {
            try {
                return JSON.parse(content, FallbackConfig.class);
            } catch (Exception e) {
                return null;
            }
        });

        if (fallbackConfig == null || fallbackConfig.getConsumerFallbacks() == null || fallbackConfig.getConsumerFallbacks().isEmpty()) {
            return;
        }
        for (FallbackConfig.ConsumerFallback config : fallbackConfig.getConsumerFallbacks()) {
            if (serviceName.equals(config.getServiceName()) && config.isFallback()) {
                throw new BusinessFeignException(HTTP_OK, ErrorCode.REQUEST_BLOCK.getErrorCode(), ErrorCode.REQUEST_BLOCK.getErrorMsg());
            }
        }
    }

}

