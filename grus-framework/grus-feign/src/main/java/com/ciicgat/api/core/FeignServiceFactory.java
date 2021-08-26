/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.google.common.annotations.VisibleForTesting;
import feign.Client;
import feign.Request;
import feign.Retryer;

/**
 * 本类，采用静态工厂模式
 * <p>
 * 服务默认连接延迟为10s，读取延迟为60s。如果有大的批量任务，或者特殊场景，请自行设置 {@link Request.Options}
 * <p>
 * 服务默认retry策略为失败之后不重试。对于某些读接口，可以定义自己的retry策略，比如重试三次
 * <p>
 * 定义自己的requestInterceptors，可以拦截服务的http请求,方便框架上做一些事。
 * <p>
 * 定义服务的ServiceEndPoint值时，可以定义到package或class级别。默认优先从class级别读取，读取不到时，再去读package的。
 * <p>
 * 注意:service的实例是有缓存的。如果需要定义同一个serviceClazz的不同实例，需要调用removeCache方法
 * <p>
 * 注意所有的配置是到Class级别，而不是method级别
 * <p>
 * Created by August.Zhou on 2017/7/28 14:17.
 */
@SuppressWarnings("unchecked")
public class FeignServiceFactory {


    private FeignServiceFactory() {
    }


    public static void removeCache(final Class<?> serviceClazz) {
        FeignServiceBuilder.SERVICE_CACHE.remove(new ServiceCacheKey(serviceClazz, null, null, false, false));
    }

    public static <T> T newInstance(final Class<T> serviceClazz) {
        return newInstance(serviceClazz,
                null, null, null, true);
    }


    @VisibleForTesting
    public static <T> T newInstance(final Class<T> serviceClazz, Client client) {
        return newInstance(serviceClazz,
                null, null, client, false);
    }


    public static <T> T newInstance(final Class<T> serviceClazz, Request.Options options) {
        return newInstance(serviceClazz,
                options, null, null, false);
    }

    public static <T> T newInstance(final Class<T> serviceClazz, Request.Options options,
                                    Retryer retryer, Client client, boolean fromCache) {
        return FeignServiceBuilder
                .newBuilder()
                .serviceClazz(serviceClazz)
                .options(options)
                .retryer(retryer)
                .client(client)
                .fromCache(fromCache)
                .build();
    }


}
