/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import com.ciicgat.sdk.servlet.GrusFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author August.Zhou
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @date 2020-11-12 17:16
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ServletContextInitializer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({ApiResponseHeaderAdvice.class, GlobalExceptionHandler.class, GrusSystemController.class, GrusHealthCheckController.class})
@EnableConfigurationProperties(WebProperties.class)
public class GrusWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GrusFilter grusFilter() {
        return new GrusFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean grusFilterRegistrationBean(GrusFilter grusFilter) {
        FilterRegistrationBean<GrusFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(grusFilter);
        registration.addUrlPatterns("/*");
        registration.setName("grusFilter");
        registration.setOrder(0);
        return registration;
    }

}
