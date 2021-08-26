/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.grus.service.naming.LocalNamingService;

import java.util.Objects;

/**
 * Created by August.Zhou on 2017/8/1 9:33.
 */
public class EndPointConfigs {


    public static void addEndPointConfig(String appName, String baseUrl) {
        LocalNamingService.put(Objects.requireNonNull(appName), Objects.requireNonNull(baseUrl).trim());
    }


}
