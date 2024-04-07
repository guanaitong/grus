/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.webflux.client;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.service.Services;
import com.ciicgat.grus.service.naming.NamingService;
import com.ciicgat.grus.webflux.client.trace.TraceInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by August.Zhou on 2023/10/16 14:33
 */
public class SpringServiceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringServiceBuilder.class);

    private SpringServiceBuilder() {
    }

    public static SpringServiceBuilder newBuilder() {
        return new SpringServiceBuilder();
    }

    static final ConcurrentMap<ServiceCacheKey, Object> SERVICE_CACHE = new ConcurrentHashMap<>();

    static final ConcurrentMap<String, WebClient> WEB_CLIENT_CACHE = new ConcurrentHashMap<>();

    private Class<?> serviceClazz;

    private boolean fromCache;

    private NamingService namingService = NamingService.DEFAULT;

    private Duration blockTimeout;

    public SpringServiceBuilder serviceClazz(Class<?> serviceClazz) {
        this.serviceClazz = serviceClazz;
        return this;
    }

    public SpringServiceBuilder fromCache(boolean fromCache) {
        this.fromCache = fromCache;
        return this;
    }

    public SpringServiceBuilder namingService(NamingService namingService) {
        this.namingService = namingService;
        return this;
    }

    public SpringServiceBuilder blockTimeout(Duration blockTimeout) {
        this.blockTimeout = blockTimeout;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T build() {
        Objects.requireNonNull(serviceClazz);
        Objects.requireNonNull(namingService);
        if (!fromCache) {
            return (T) create();
        }
        ServiceCacheKey serviceCacheKey = new ServiceCacheKey(serviceClazz, this.blockTimeout);
        Object serviceInstance = SERVICE_CACHE.get(serviceCacheKey);
        if (serviceInstance != null) {
            return (T) serviceInstance;
        }

        T newServiceInstance = create();
        serviceInstance = SERVICE_CACHE.putIfAbsent(serviceCacheKey, newServiceInstance);
        if (serviceInstance == null) {
            serviceInstance = newServiceInstance;
        }
        return (T) serviceInstance;
    }

    @SuppressWarnings("unchecked")
    private <T> T create() {
        ServiceName serviceName = Services.getServiceName(this.serviceClazz);

        WebClient client = WEB_CLIENT_CACHE.compute(serviceName.value(), (s, webClient) -> {
            String baseUrl = namingService.resolveBaseUrl(serviceName, Services.isSecure(this.serviceClazz));
            return WebClient.builder().baseUrl(baseUrl)
//                .defaultStatusHandler()
                    .filter(new TraceInterceptor())
//                .codecs(t->t.customCodecs().registerWithDefaultConfig())
                    .build();
        });
        WebClientAdapter webClientAdapter = WebClientAdapter.create(client);
        webClientAdapter.setBlockTimeout(this.blockTimeout);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(webClientAdapter)
//                .customArgumentResolver()
                .build();
        return (T) httpServiceProxyFactory.createClient(this.serviceClazz);
    }

}
