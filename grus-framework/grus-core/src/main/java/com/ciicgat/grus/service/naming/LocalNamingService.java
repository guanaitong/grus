/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by August.Zhou on 2018-11-21 14:50.
 */
public class LocalNamingService implements NamingService {

    private static final ConcurrentMap<String, String> CUSTOM_END_POINT_CONFIGS = new ConcurrentHashMap<>();

    @Override
    public String resolve(String serviceName) {
        return CUSTOM_END_POINT_CONFIGS.get(serviceName);
    }

    public static void put(String appName, String baseUrl) {
        CUSTOM_END_POINT_CONFIGS.put(Objects.requireNonNull(appName), Objects.requireNonNull(baseUrl).trim());
    }
}
