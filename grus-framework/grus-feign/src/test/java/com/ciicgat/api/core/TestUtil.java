/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by August.Zhou on 2018-10-19 10:07.
 */
public class TestUtil {

    public static <T> Pair<T, MockWebServer> newInstance(final String appName, final Supplier<T> supplier) {
        while (true) {
            try {
                MockWebServer mockWebServer = new MockWebServer();
                int port = new Random().nextInt(20000);
                mockWebServer.start(port);
                EndPointConfigs.addEndPointConfig(appName, "http://127.0.0.1:" + port);
                return Pair.of(supplier.get(), mockWebServer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> Pair<T, MockWebServer> newInstance(final String appName, final Class<T> serviceClazz) {
        return newInstance(appName, () -> FeignServiceBuilder
                .newBuilder()
                .serviceClazz(serviceClazz)
                .logReq(true)
                .logResp(true)
                .build());
    }

    public static <T> Pair<T, MockWebServer> newInstance(final String appName, final Class<T> serviceClazz, boolean enableSentinel) {
        return newInstance(appName, () -> FeignServiceBuilder
                .newBuilder()
                .serviceClazz(serviceClazz)
                .logReq(true)
                .logResp(true)
                .enableSentinel(enableSentinel)
                .build());
    }

    public static <T> Pair<T, MockWebServer> newInstance(final String appName, final Class<T> serviceClazz, boolean enableSentinel, FallbackFactory<T> factory) {
        return newInstance(appName, () -> FeignServiceBuilder
                .newBuilder()
                .fallbackFactory(factory)
                .serviceClazz(serviceClazz)
                .logReq(true)
                .logResp(true)
                .enableSentinel(enableSentinel)
                .build());
    }


}
