/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import com.ciicgat.sdk.util.ComponentStatus;
import com.ciicgat.sdk.util.http.metrics.DelegateEventListener;
import com.ciicgat.sdk.util.http.trace.OkhttpTracingInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 对SSL进行了特别的处理，信任关爱通自己签发的证书
 * httpClient单例
 * Created by August.Zhou on 2017/8/3 9:10.
 */
public class HttpClientSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientSingleton.class);


    public static void setTrustAny(boolean trustAny) {
        SSL.trustAny = trustAny;
    }

    /**
     * 获取okHttpClient单例
     *
     * @return
     */
    public static okhttp3.OkHttpClient getOkHttpClient() {
        return OkHttpClientSingletonHolder.OK_HTTP_CLIENT;
    }

    /**
     * 获取apacheHttpClient单例
     *
     * @return
     */
    public static org.apache.http.impl.client.CloseableHttpClient getApacheHttpClient() {
        return ApacheHttpClientSingletonHolder.CLOSEABLE_HTTP_CLIENT;
    }

    private static class OkHttpClientSingletonHolder {
        private static final okhttp3.OkHttpClient OK_HTTP_CLIENT;

        static {
            ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<>(), Util.threadFactory("OkHttp Dispatcher", false));


            Dispatcher dispatcher = new Dispatcher(executorService);
            dispatcher.setMaxRequestsPerHost(250);
            dispatcher.setMaxRequests(2000);

            OkHttpClient.Builder clientBuilder = new okhttp3.OkHttpClient
                    .Builder()
                    .eventListener(DelegateEventListener.getForCoreInstance())
                    .dispatcher(dispatcher)
                    .dns(CacheDnsResolver.INSTANCE)
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .connectionPool(new ConnectionPool(128, 900, TimeUnit.SECONDS))
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .sslSocketFactory(SSL.sslSocketFactory, SSL.x509TrustManager)
                    .hostnameVerifier(SSL.hostnameVerifier);

            if (ComponentStatus.isTraceEnable()) {
                clientBuilder.addInterceptor(new OkhttpTracingInterceptor());
            }
            OK_HTTP_CLIENT = clientBuilder.build();
        }
    }

    private static class ApacheHttpClientSingletonHolder {
        private static final org.apache.http.impl.client.CloseableHttpClient CLOSEABLE_HTTP_CLIENT;


        static {
            org.apache.http.client.config.RequestConfig requestConfig = org.apache.http.client.config.RequestConfig
                    .custom()
                    .setRedirectsEnabled(false)
                    .setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10))
                    .setConnectionRequestTimeout((int) TimeUnit.MINUTES.toMillis(1))
                    .setSocketTimeout((int) TimeUnit.MINUTES.toMillis(1))
                    .build();

            org.apache.http.impl.client.HttpClientBuilder builder = HttpClientBuilder
                    .create()
                    .setUserAgent("JAVA_APACHE_HTTP_CLIENT")
                    .setMaxConnPerRoute(500)
                    .setMaxConnTotal(2000)
                    .setDefaultRequestConfig(requestConfig)
                    .setDnsResolver(ApacheCacheDnsResolver.INSTANCE)
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(
                            SSL.sslSocketFactory, SSL.hostnameVerifier));

            CLOSEABLE_HTTP_CLIENT = builder.build();

        }
    }


}
