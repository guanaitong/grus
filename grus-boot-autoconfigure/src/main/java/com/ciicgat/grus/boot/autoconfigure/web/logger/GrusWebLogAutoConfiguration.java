/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GrusWebLogAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public GrusWebLogPrinter grusWebLogPrinter() {

        return new GrusWebLogPrinter();
    }
}
