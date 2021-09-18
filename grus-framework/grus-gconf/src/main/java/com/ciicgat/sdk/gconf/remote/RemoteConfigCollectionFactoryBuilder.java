/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.sdk.gconf.ConfigCollectionFactory;
import com.ciicgat.sdk.gconf.GconfConfig;
import com.ciicgat.sdk.gconf.noop.NoopConfigCollectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * 客户端实现
 * Created by August.Zhou on 2016/10/19 14:17.
 */
public class RemoteConfigCollectionFactoryBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteConfigCollectionFactoryBuilder.class);


    public static ConfigCollectionFactory getInstance() {
        return CollectionFactoryHolder.CONFIG_COLLECTION_FACTORY;
    }

    private static class CollectionFactoryHolder {
        private static final ConfigCollectionFactory CONFIG_COLLECTION_FACTORY;

        static {
            ConfigCollectionFactory configCollectionFactory = null;
            try {
                InetAddress[] inetAddresses = InetAddress.getAllByName(GconfConfig.DOMAIN);
                configCollectionFactory = new RemoteConfigCollectionFactory(GconfConfig.DOMAIN);
                LOGGER.info("use remote, domain {} ,target {}", GconfConfig.DOMAIN, Arrays.asList(inetAddresses));
            } catch (Throwable e) {
                LOGGER.warn("use noop,cannot resolve gconf domain " + GconfConfig.DOMAIN, e);
                configCollectionFactory = new NoopConfigCollectionFactory();
            }

            CONFIG_COLLECTION_FACTORY = configCollectionFactory;
        }
    }


}
