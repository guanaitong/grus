/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import com.ciicgat.grus.service.GrusRuntimeConfig;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by August.Zhou on 2019-06-19 12:50.
 */
public class GconfGrusRuntimeConfig implements GrusRuntimeConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(GconfGrusRuntimeConfig.class);

    private final static String KEY = "grus-config.properties";


    private ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection();


    @Override
    public boolean getBoolValue(String key, boolean defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            LOGGER.error(value, e);
            return defaultValue;
        }
    }

    @Override
    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            LOGGER.error(value, e);
            return defaultValue;
        }
    }

    @Override
    public String getStringValue(String key, String defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private String getValue(String key) {
        if (this.configCollection == null) {
            return null;
        }
        Properties properties = this.configCollection.getProperties(KEY);
        return properties == null ? null : properties.getProperty(key);
    }
}
