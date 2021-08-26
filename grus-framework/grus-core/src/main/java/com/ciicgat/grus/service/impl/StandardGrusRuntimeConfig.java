/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.impl;

import com.ciicgat.grus.service.GrusRuntimeConfig;

/**
 * Created by August.Zhou on 2019-06-19 12:36.
 */
public class StandardGrusRuntimeConfig implements GrusRuntimeConfig {


    @Override
    public boolean getBoolValue(String key, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public int getIntValue(String key, int defaultValue) {
        return defaultValue;
    }

    @Override
    public String getStringValue(String key, String defaultValue) {
        return defaultValue;
    }

}
