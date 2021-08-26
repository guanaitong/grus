/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.noop;


import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.ConfigCollectionFactory;

/**
 * Created by August.Zhou on 2016/10/19 14:27.
 */
public class NoopConfigCollectionFactory implements ConfigCollectionFactory {

    private static final ConfigCollection INSTANCE = new NoopConfigCollection();


    public NoopConfigCollectionFactory() {
    }


    @Override
    public ConfigCollection getConfigCollection(String configCollectionId) {
        return INSTANCE;
    }

    @Override
    public ConfigCollection getConfigCollection() {
        return INSTANCE;
    }

}
