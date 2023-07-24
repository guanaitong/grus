/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.meter;

import com.ciicgat.grus.boot.autoconfigure.web.WebProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-08-08 17:34.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MeterRegistry.class)
@EnableConfigurationProperties(WebProperties.class)
public class MeterAutoConfiguration {


    @Bean
    MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(@Value("${spring.application.name}") String appName) {
        return registry -> registry.config().commonTags("application", appName);
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClientMeterBinder okHttpClientMeterBinder() {
        return new OkHttpClientMeterBinder();
    }

}
