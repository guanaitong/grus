/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;


import com.ciicgat.sdk.gconf.ConfigApp;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.ConfigCollectionFactory;
import com.ciicgat.sdk.gconf.GconfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by August.Zhou on 2016/10/19 14:27.
 */
class RemoteConfigCollectionFactory implements ConfigCollectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteConfigCollectionFactory.class);
    private ConcurrentMap<String, ConfigCollection> cache = new ConcurrentHashMap<>(4);


    private final GConfHttpClient gConfHttpClient;

    private final DataStore dataStore;
    private final GconfConfig gconfConfig;


    RemoteConfigCollectionFactory(GconfConfig gconfConfig) {
        this.gconfConfig = gconfConfig;
        this.gConfHttpClient = new GConfHttpClient(gconfConfig);
        this.dataStore = new DataStore(gConfHttpClient);
    }

    @Override
    public ConfigCollection getConfigCollection(String configCollectionId) {
        ConfigCollection configCollection = cache.get(configCollectionId);
        if (configCollection != null) {
            return configCollection;
        }
        synchronized (this) {
            configCollection = cache.get(configCollectionId);
            if (configCollection != null) {
                return configCollection;
            }
            ConfigApp configApp = gConfHttpClient.getConfigApp(configCollectionId);
            if (configApp == null) {
                LOGGER.warn("config not exist for App {}", configCollectionId);
                return null;
            }
            Map<String, ValueReference> cache = dataStore.getValueReferences(configCollectionId);
            configCollection = new RemoteConfigCollection(configApp, cache, dataStore);

            this.cache.put(configCollectionId, configCollection);
            return configCollection;
        }

    }

    @Override
    public ConfigCollection getConfigCollection() {
        if (Objects.isNull(gconfConfig.getAppName())) {
            return getConfigCollection(System.getenv("WORK_ENV"));
        }
        return getConfigCollection(gconfConfig.getAppName());
    }

}
