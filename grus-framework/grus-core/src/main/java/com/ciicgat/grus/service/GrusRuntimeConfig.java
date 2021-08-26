/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

/**
 * Created by August.Zhou on 2019-06-19 12:33.
 */
public interface GrusRuntimeConfig {


    boolean getBoolValue(String key, boolean defaultValue);

    int getIntValue(String key, int defaultValue);

    String getStringValue(String key, String defaultValue);

}
