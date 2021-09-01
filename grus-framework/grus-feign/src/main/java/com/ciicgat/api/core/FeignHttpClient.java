/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.language.FeignLanguageInterceptor;
import com.ciicgat.api.core.trace.FeignTracingInterceptor;
import com.ciicgat.grus.language.LanguageConstant;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.ConfigCollectionFactory;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.util.ComponentStatus;
import com.ciicgat.sdk.util.http.CacheDnsResolver;
import com.ciicgat.sdk.util.http.SSL;
import com.ciicgat.sdk.util.http.metrics.DelegateEventListener;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ciicgat.api.core.contants.TimeOutConstants.DEFAULT_CONNECT_TIMEOUT_MILLIS;
import static com.ciicgat.api.core.contants.TimeOutConstants.DEFAULT_READ_TIMEOUT_MILLIS;
import static com.ciicgat.api.core.contants.TimeOutConstants.DEFAULT_WRITE_TIMEOUT_MILLIS;

/**
 * 使用独立的httpclient，方便后续做扩展
 * Created by August.Zhou on 2019-07-03 9:44.
 */
class FeignHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeignHttpClient.class);

    static final String CONNECT_TIMEOUT_TAG = "xxx-inner-tag-connect";
    static final String READ_TIMEOUT_TAG = "xxx-inner-tag-read";
    static final String K8S_TARGET_TAG = "xxx-inner-tag-k8s-tag";

    private static final okhttp3.OkHttpClient OK_HTTP_CLIENT;

    static {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), Util.threadFactory("Feign OkHttp Dispatcher", false));


        Dispatcher dispatcher = new Dispatcher(executorService);
        dispatcher.setMaxRequestsPerHost(1000);
        dispatcher.setMaxRequests(5000);


        Integer maxTotalConnections = 512;
        Long timeToLive = 1800L;

        OkHttpClient.Builder clientBuilder = new okhttp3.OkHttpClient
                .Builder()
                .eventListener(DelegateEventListener.getForFeignInstance())
                .dispatcher(dispatcher)
                .dns(CacheDnsResolver.INSTANCE)
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(maxTotalConnections, timeToLive, TimeUnit.SECONDS))
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .sslSocketFactory(SSL.sslSocketFactory, SSL.x509TrustManager)
                .hostnameVerifier(SSL.hostnameVerifier);
        if (ComponentStatus.isTraceEnable()) {
            clientBuilder.addInterceptor(new FeignTracingInterceptor());
        }
        try {
            ConfigCollectionFactory configCollectionFactory = RemoteConfigCollectionFactoryBuilder.getInstance();
            ConfigCollection configCollection = configCollectionFactory.getConfigCollection();

            if (configCollection != null
                    && StringUtils.isNotEmpty(configCollection.getConfig(LanguageConstant.LANG_PROPERTIES))) {
                clientBuilder.addInterceptor(new FeignLanguageInterceptor());
            }
        } catch (Exception e) {
            // do nothing
        }

        OK_HTTP_CLIENT = clientBuilder.build();
    }

    /**
     * 获取okHttpClient单例
     *
     * @return
     */
    static okhttp3.OkHttpClient getOkHttpClient() {
        return OK_HTTP_CLIENT;
    }


}
