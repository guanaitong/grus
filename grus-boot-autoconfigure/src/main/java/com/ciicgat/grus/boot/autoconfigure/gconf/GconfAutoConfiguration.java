/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import com.ciicgat.grus.boot.autoconfigure.core.AppName;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.ConfigCollectionFactory;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-01-28 17:55.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ConfigCollectionFactory.class)
@ConditionalOnProperty(prefix = "grus.gconf", value = "enabled", havingValue = "true", matchIfMissing = true)
public class GconfAutoConfiguration {

    @AppName
    private String appName;

    @Bean
    @ConditionalOnMissingBean
    public ConfigCollectionFactory configCollectionFactory() {
        return RemoteConfigCollectionFactoryBuilder.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigCollection configCollection(ConfigCollectionFactory configCollectionFactory) {
        return configCollectionFactory.getConfigCollection(appName);
    }

}
