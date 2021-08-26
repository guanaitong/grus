/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.contants.TimeOutConstants;
import com.ciicgat.api.core.form.FormEncoder;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.GrusServiceStatus;
import com.ciicgat.grus.service.naming.NamingService;
import com.ciicgat.sdk.util.ComponentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.slf4j.Slf4jLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    private Class<?> serviceClazz;
    private Request.Options options;

    private Retryer retryer = new RetryWith502.RetryerImpl();

    private boolean fromCache;
    private CacheOptions cacheOptions;
    private Client client;
    private NamingService namingService = NamingService.DEFAULT;
    private FallbackFactory<?> fallbackFactory;
    private List<RequestInterceptor> requestInterceptors = new ArrayList<>();
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

    public FeignServiceBuilder fallbackFactory(FallbackFactory<?> fallbackFactory) {
        this.fallbackFactory = fallbackFactory;
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
        ServiceCacheKey serviceCacheKey = new ServiceCacheKey(serviceClazz, cacheOptions, options, logReq, logResp);
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
            options = new Request.Options(TimeOutConstants.DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeOutConstants.DEFAULT_READ_TIMEOUT_MILLIS);
        }
        if (retryer == null) {
            retryer = new RetryerConnectTimeout();
        }
        ServiceName serviceName = getServiceName();

        Objects.requireNonNull(serviceName.value(), "serviceName cannot be null");


        if (client == null) {
            client = new OkHttpClientWrapper(FeignHttpClient.getOkHttpClient());
        }

        GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();

        GrusServiceStatus grusServiceStatus = grusRuntimeManager.registerDownstreamService(serviceName.value(), "");


        Object serviceInstance = Feign.builder()
                .logger(new Slf4jLogger())
                .contract(getContract())
                .options(options)
                .logLevel(feign.Logger.Level.NONE)
                .requestInterceptors(requestInterceptors)
                .decoder(new OptionalDecoder(new JacksonDecoder(OBJECT_MAPPER)))
                .encoder(new FormEncoder(new JacksonEncoder(OBJECT_MAPPER)))
                .errorDecoder(new ErrorDecoder.Default())
                .invocationHandlerFactory(new GInvocationHandlerFactory(grusServiceStatus, cacheOptions, fallbackFactory, this.logReq, this.logResp))
                .client(client)
                .retryer(retryer)//默认不retry
                .target(new GrusTarget(serviceClazz, serviceName, namingService));
//                .target(serviceClazz, baseUrl);

        return (T) serviceInstance;
    }

    private Contract getContract() {
        if (!ComponentStatus.isSpringMvcEnable()) {
            return new GrusContract();
        }
        return GrusFeignContractUtil.getFeignContract(serviceClazz);
    }


    /**
     * 找appName，优先在类上找，其次才是包
     *
     * @return
     */
    private ServiceName getServiceName() {
        ServiceName serviceEndPoint = serviceClazz.getAnnotation(ServiceName.class);
        if (serviceEndPoint != null) {
            return serviceEndPoint;
        }

        serviceEndPoint = serviceClazz.getPackage().getAnnotation(ServiceName.class);
        if (serviceEndPoint != null) {
            return serviceEndPoint;
        }

        throw new RuntimeException("cannot find the appName");
    }


}
