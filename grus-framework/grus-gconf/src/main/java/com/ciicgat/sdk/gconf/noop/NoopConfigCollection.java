/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.noop;

import com.ciicgat.sdk.gconf.BeanLoader;
import com.ciicgat.sdk.gconf.ConfigApp;
import com.ciicgat.sdk.gconf.ConfigChangeListener;
import com.ciicgat.sdk.gconf.ConfigCollection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by August.Zhou on 2018/8/23 10:12.
 */
class NoopConfigCollection implements ConfigCollection {

    @Override
    public ConfigApp getConfigCollectionInfo() {
        return ConfigApp.NOOP_INSTANCE;
    }


    @Override
    public String getConfig(String key) {
        return "";
    }

    Properties properties = new Properties();

    @Override
    public Properties getProperties(String key) {
        return properties;
    }

    Map<String, Object> jsonObject = new HashMap<>();

    @Override
    public Map<String, Object> getJSONObject(String key) {
        return jsonObject;
    }

    @Override
    public <T> T getBean(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getLatestBean(String key, Class<T> clazz) {
        return null;
    }


    @Override
    public <T> T getBean(String key, BeanLoader<T> beanLoader) {
        return null;
    }

    @Override
    public void addConfigChangeListener(String key, ConfigChangeListener configChangeListener) {

    }

    @Override
    public void addGlobalConfigChangeListener(ConfigChangeListener configChangeListener) {

    }

    @Override
    public Map<String, String> asMap() {
        return Collections.emptyMap();
    }
}
