/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;

/**
 * Created by August.Zhou on 2019-06-25 13:08.
 */
public class GlobalGconfConfig {

    private static final ConfigCollection GRUS_CONFIG_COLLECTION = RemoteConfigCollectionFactoryBuilder
            .getInstance()
            .getConfigCollection("grus");

    public static ConfigCollection getConfig() {
        return GRUS_CONFIG_COLLECTION;
    }

}
