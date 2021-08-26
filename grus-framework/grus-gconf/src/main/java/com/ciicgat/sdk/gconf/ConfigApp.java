/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by August.Zhou on 2016/10/11 10:50.
 */
public class ConfigApp implements Serializable {
    public static final ConfigApp NOOP_INSTANCE = new ConfigApp();

    private String configCollectionId;

    private String name;        //应用名称

    public ConfigApp(String configCollectionId) {
        this.configCollectionId = configCollectionId;
    }

    public ConfigApp() {
    }


    public String getConfigCollectionId() {
        return configCollectionId;
    }

    public void setConfigCollectionId(String configCollectionId) {
        this.configCollectionId = configCollectionId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigApp)) return false;
        ConfigApp configApp = (ConfigApp) o;
        return Objects.equals(configCollectionId, configApp.configCollectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configCollectionId);
    }

    @Override
    public String toString() {
        return "ConfigApp{" +
                "configCollectionId='" + configCollectionId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
