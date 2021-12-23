/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by August.Zhou on 2021/12/23 15:01.
 */
public class RestClients {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClients.class);
    private static final String SPAN_KEY = "__span__";

    public static RestClientBuilder builder(HttpHost... hosts) {
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                LOGGER.info("failedNoe {}", node);
            }
        });
        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.addInterceptorFirst((HttpRequestInterceptor) (request, context) -> context.setAttribute(SPAN_KEY, System.nanoTime()));
            httpClientBuilder.addInterceptorFirst((HttpResponseInterceptor) (response, context) -> {
                Object attribute = context.getAttribute(SPAN_KEY);
                if (attribute instanceof Long start) {
                    SlowLogger.logEvent(Module.ELASTICSEARCH, System.nanoTime() - start.longValue(), "es slow");
                }
            });
            return httpClientBuilder;
        });
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder);
        return restClientBuilder;
    }
}
