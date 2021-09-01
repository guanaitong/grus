/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.sdk.gconf.BeanLoader;
import com.ciicgat.sdk.gconf.ConfigApp;
import com.ciicgat.sdk.gconf.ConfigChangeListener;
import com.ciicgat.sdk.gconf.ConfigCollection;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by August.Zhou on 2016/10/19 14:27.
 */
@SuppressWarnings("unchecked")
class RemoteConfigCollection implements ConfigCollection {


    private final ConfigApp configApp;

    private final Map<String, ValueReference> localCache;

    private final DataStore dataStore;


    RemoteConfigCollection(ConfigApp configApp, Map<String, ValueReference> localCache, DataStore dataStore) {
        this.configApp = configApp;
        this.localCache = localCache;
        this.dataStore = dataStore;
    }

    @Override
    public ConfigApp getConfigCollectionInfo() {
        return configApp;
    }


    @Override
    public String getConfig(String key) {
        ValueReference valueReference = localCache.get(key);
        if (Objects.nonNull(valueReference)) {
            return valueReference.getRaw();
        }
        return null;
    }

    @Override
    public Properties getProperties(String key) {
        ValueReference valueReference = localCache.get(key);
        if (Objects.nonNull(valueReference)) {
            return valueReference.asProperties();
        }
        return null;
    }

    @Override
    public Map<String, Object> getJSONObject(String key) {
        ValueReference valueReference = localCache.get(key);
        if (Objects.nonNull(valueReference)) {
            return valueReference.asJSONObject();
        }
        return null;
    }

    @Override
    public <T> T getBean(final String key, Class<T> clazz) {
        ValueReference valueReference = localCache.get(key);
        if (Objects.nonNull(valueReference)) {
            return valueReference.asBean(clazz);
        }
        return null;
    }

    @Override
    public <T> T getLatestBean(String key, Class<T> clazz) {
        ValueReference valueReference = localCache.get(key);
        if (Objects.nonNull(valueReference)) {
            return valueReference.asLatestBean(clazz);
        }
        return null;
    }


    @Override
    public <T> T getBean(String key, BeanLoader<T> beanLoader) {
        ValueReference valueReference = localCache.get(key);
        if (Objects.nonNull(valueReference)) {
            return valueReference.asBean(beanLoader);
        }
        return null;
    }

    @Override
    public void addConfigChangeListener(String key, ConfigChangeListener configChangeListener) {
        ValueReference valueReference = localCache.get(key);
        if (Objects.nonNull(valueReference)) {
            valueReference.addConfigChangeListener(configChangeListener);
        }
    }

    @Override
    public void addGlobalConfigChangeListener(ConfigChangeListener configChangeListener) {
        dataStore.addGlobalConfigChangeListener(this.configApp.getConfigCollectionId(), configChangeListener);
    }

    @Override
    public Map<String, String> asMap() {
        Map<String, String> res = new LinkedHashMap<>(localCache.size());
        for (Map.Entry<String, ValueReference> entry : localCache.entrySet()) {
            res.put(entry.getKey(), entry.getValue().getRaw());
        }
        return Collections.unmodifiableMap(res);
    }


}
