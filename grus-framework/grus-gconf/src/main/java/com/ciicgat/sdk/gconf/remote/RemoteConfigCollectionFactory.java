/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;


import com.ciicgat.sdk.gconf.ConfigApp;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.ConfigCollectionFactory;
import com.ciicgat.sdk.util.system.Systems;
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


    RemoteConfigCollectionFactory(GConfHttpClient gConfHttpClient) {
        this.gConfHttpClient = gConfHttpClient;
        this.dataStore = new DataStore(gConfHttpClient);
    }

    RemoteConfigCollectionFactory(String domain) {
        this(new GConfHttpClient(domain));
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
        if (Objects.equals(Systems.APP_NAME, Systems.UNKNOWN)) {
            throw new RuntimeException("请设置APP_NAME环境变量");
        }
        return getConfigCollection(Systems.APP_NAME);
    }

}
