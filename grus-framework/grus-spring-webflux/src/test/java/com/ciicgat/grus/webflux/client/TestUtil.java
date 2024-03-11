/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.webflux.client;

import com.ciicgat.api.core.EndPointConfigs;
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
        return newInstance(appName, () -> SpringServiceFactory.newInstance(serviceClazz));
    }

}
