/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.springservice;

import java.time.Duration;

/**
 * Created by August.Zhou on 2023/9/6 13:08
 */
public class SpringServiceFactory {
    public static <T> T newInstance(final Class<T> serviceClazz) {
        return newInstance(serviceClazz, null);
    }

    public static <T> T newInstance(final Class<T> serviceClazz, Duration blockTimeout) {
        return SpringServiceBuilder
                .newBuilder()
                .serviceClazz(serviceClazz)
                .fromCache(true)
                .blockTimeout(blockTimeout)
                .build();
    }
}
