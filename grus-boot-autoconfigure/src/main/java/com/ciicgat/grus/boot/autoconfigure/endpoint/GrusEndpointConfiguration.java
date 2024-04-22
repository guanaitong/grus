/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.endpoint;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Created by August.Zhou on 2022/4/1 17:48
 */
@ConditionalOnClass(Endpoint.class)
public class GrusEndpointConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public GrusServiceDependencyEndpoint grusServiceDependencyEndpoint() {
        return new GrusServiceDependencyEndpoint();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public GrusVersionEndpoint grusVersionEndpoint() {
        return new GrusVersionEndpoint();
    }

}
