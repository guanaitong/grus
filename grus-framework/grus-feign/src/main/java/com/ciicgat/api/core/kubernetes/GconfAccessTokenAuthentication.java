/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

import com.ciicgat.grus.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.gconf.ConfigCollection;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.credentials.Authentication;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @Author: August
 * @Date: 2021/7/15 22:35
 */
public class GconfAccessTokenAuthentication implements Authentication, Interceptor {
    private ConfigCollection configCollection = GlobalGconfConfig.getConfig();


    @Override
    public void provide(ApiClient client) {
        OkHttpClient withInterceptor = client.getHttpClient().newBuilder().addInterceptor(this).build();
        client.setHttpClient(withInterceptor);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest;
        newRequest = request.newBuilder().header("Authorization", "Bearer " + configCollection.getConfig("k8s_token")).build();
        return chain.proceed(newRequest);
    }
}
