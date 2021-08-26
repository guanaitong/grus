/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import com.ciicgat.api.core.FeignServiceBuilder;
import com.ciicgat.grus.service.naming.NamingService;
import com.ciicgat.sdk.gconf.support.GconfEndPointNamingService;
import com.ciicgat.sdk.util.ComponentStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-03-04 18:47.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({FeignServiceBuilder.class, feign.Client.class})
@EnableConfigurationProperties({FeignProperties.class})
public class FeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NamingService namingService() {
        if (ComponentStatus.isGconfEnable()) {
            NamingService.DEFAULT.add(2, new GconfEndPointNamingService());
        }
        return NamingService.DEFAULT;
    }

    @Bean
    public FeignServiceBeanProcessor feignServiceBeanProcessor() {
        return new FeignServiceBeanProcessor();
    }

}
