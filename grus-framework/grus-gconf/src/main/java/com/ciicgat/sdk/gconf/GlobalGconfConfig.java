/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.util.frigate.FrigateMessageConstants;
import com.ciicgat.sdk.util.frigate.LocalFrigateMessageConstants;

/**
 * Created by August.Zhou on 2019-06-25 13:08.
 */
public class GlobalGconfConfig {

    static {
        LocalFrigateMessageConstants.DEFAULT = new FrigateMessageConstants() {
            @Override
            public String getBaseUrl() {
                return getConfig().getProperties("frigate.properties").getProperty("frigate.message.base.url");
            }
        };
    }

    private static ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder
            .getInstance()
            .getConfigCollection("grus");

    public static ConfigCollection getConfig() {
        return configCollection;
    }

}
