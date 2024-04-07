/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.contants.TimeOutConstants;
import com.ciicgat.api.core.form.FormEncoder;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.GrusServiceStatus;
import com.ciicgat.grus.service.Services;
import com.ciicgat.grus.service.naming.NamingService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.annotations.VisibleForTesting;
import feign.Client;
import feign.Feign;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.slf4j.Slf4jLogger;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2019-03-06 17:12.
 */
@SuppressWarnings("unchecked")
public class FeignServiceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignServiceBuilder.class);

    private FeignServiceBuilder() {
    }

    public static FeignServiceBuilder newBuilder() {
        return new FeignServiceBuilder();
    }


    static final ConcurrentMap<ServiceCacheKey, Object> SERVICE_CACHE = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    private Class<?> serviceClazz;
    private Request.Options options;

    private Retryer retryer;

    private boolean fromCache;
    private CacheOptions cacheOptions;
    private Client client;
    private NamingService namingService = NamingService.DEFAULT;
    private FallbackFactory<?> fallbackFactory;

    private List<String> ignoreErrorMethods = new ArrayList<>();
    private List<RequestInterceptor> requestInterceptors = new ArrayList<>();
    private Interceptor interceptor;
    private boolean logReq;

    private boolean logResp;

    public FeignServiceBuilder serviceClazz(Class<?> serviceClazz) {
        this.serviceClazz = serviceClazz;
        return this;
    }

    public FeignServiceBuilder options(Request.Options options) {
        this.options = options;
        return this;
    }

    public FeignServiceBuilder retryer(Retryer retryer) {
        this.retryer = retryer;
        return this;
    }

    public FeignServiceBuilder fromCache(boolean fromCache) {
        this.fromCache = fromCache;
        return this;
    }

    public FeignServiceBuilder cacheOptions(CacheOptions cacheOptions) {
        this.cacheOptions = cacheOptions;
        return this;
    }

    public FeignServiceBuilder namingService(NamingService namingService) {
        this.namingService = namingService;
        return this;
    }

    public FeignServiceBuilder addIgnoreErrorMethods(String... ignoreErrorMethods) {
        for (String ignoreErrorMethod : ignoreErrorMethods) {
            this.ignoreErrorMethods.add(ignoreErrorMethod);
        }
        return this;
    }

    public FeignServiceBuilder fallbackFactory(FallbackFactory<?> fallbackFactory) {
        this.fallbackFactory = fallbackFactory;
        return this;
    }

    public FeignServiceBuilder interceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public FeignServiceBuilder requestInterceptor(RequestInterceptor requestInterceptor) {
        this.requestInterceptors.add(requestInterceptor);
        return this;
    }

    public FeignServiceBuilder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
        this.requestInterceptors.clear();
        for (RequestInterceptor requestInterceptor : requestInterceptors) {
            this.requestInterceptors.add(requestInterceptor);
        }
        return this;
    }

    public FeignServiceBuilder logReq(boolean logReq) {
        this.logReq = logReq;
        return this;
    }

    public FeignServiceBuilder logResp(boolean logResp) {
        this.logResp = logResp;
        return this;
    }

    @VisibleForTesting
    public FeignServiceBuilder client(Client client) {
        this.client = client;
        return this;
    }

    public <T> T build() {
        Objects.requireNonNull(serviceClazz);
        Objects.requireNonNull(namingService);

        if (!fromCache) {
            return (T) create();
        }
        ServiceCacheKey serviceCacheKey = new ServiceCacheKey(serviceClazz, cacheOptions, options, logReq, logResp, ignoreErrorMethods);
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


    private <T> T create() {
        if (options == null) {
            options = new Request.Options(TimeOutConstants.DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS, TimeOutConstants.DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS, true);
        }
        if (retryer == null) {
            retryer = new ConfigRetryer();
        }
        ServiceName serviceName = Services.getServiceName(this.serviceClazz);

        Objects.requireNonNull(serviceName.value(), "serviceName cannot be null");


        if (client == null) {
            OkHttpClient okHttpClient = FeignHttpClient.getOkHttpClient();
            if (interceptor != null) {
                okHttpClient = okHttpClient.newBuilder().addInterceptor(interceptor).build();
            }
            client = new OkHttpClientWrapper(okHttpClient);
        }

        GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();

        GrusServiceStatus grusServiceStatus = grusRuntimeManager.registerDownstreamService(serviceName.value(), "");


        try {
            Object serviceInstance = Feign.builder()
                    .logger(new Slf4jLogger())
                    .contract(GrusFeignContractUtil.getFeignContract(serviceClazz))
                    .options(options)
                    .logLevel(feign.Logger.Level.NONE)
                    .requestInterceptors(requestInterceptors)
                    .decoder(new OptionalDecoder(new JacksonDecoder(OBJECT_MAPPER)))
                    .encoder(new FormEncoder(new JacksonEncoder(OBJECT_MAPPER)))
                    .errorDecoder(new ErrorDecoder.Default())
                    .invocationHandlerFactory(new GInvocationHandlerFactory(grusServiceStatus, cacheOptions, fallbackFactory, this.logReq, this.logResp, ignoreErrorMethods))
                    .client(client)
                    .retryer(retryer)//默认不retry
                    .target(new GrusTarget(serviceClazz, serviceName, namingService));
//                .target(serviceClazz, baseUrl);

            return (T) serviceInstance;
        } catch (RuntimeException ex) {
            LOGGER.error("create bean error,serviceName {} class {}", serviceName, serviceClazz);
            throw ex;
        }
    }


}
