/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gfs;

import com.ciicgat.sdk.gfs.GfsClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by albert.sun on Jul 16, 2019
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({GfsClient.class})
public class GfsClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GfsClient gfsClient() {
        return new GfsClient();
    }

}
