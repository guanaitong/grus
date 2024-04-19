/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import com.ciicgat.grus.boot.autoconfigure.web.http2.Http2WebServerFactoryCustomizer;
import com.ciicgat.grus.boot.autoconfigure.web.live.DefaultGrusHealthChecker;
import com.ciicgat.grus.boot.autoconfigure.web.live.GrusHealthCheckController;
import com.ciicgat.grus.boot.autoconfigure.web.live.GrusHealthChecker;
import com.ciicgat.grus.sentinel.ServerRuleManager;
import com.ciicgat.sdk.servlet.GrusFilter;
import com.ciicgat.sdk.servlet.RequestIdProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@Import({ApiResponseHeaderAdvice.class, GlobalExceptionHandler.class, GrusHealthCheckController.class})
@EnableConfigurationProperties(WebProperties.class)
public class GrusWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GrusFilter grusFilter(WebProperties webProperties, RequestIdProvider requestIdProvider) {
        WebProperties.SystemRule systemRule = webProperties.getSystemRule();
        if (systemRule.isEnabled()) {
            ServerRuleManager.loadSystemRule(systemRule.getMaxThread(), systemRule.getAvgRt());
        }
        return new GrusFilter(webProperties.isDisableOldSslProtocol(), requestIdProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestIdProvider requestIdProvider() {
        return RequestIdProvider.NONE;
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<?> grusFilterRegistrationBean(GrusFilter grusFilter) {
        FilterRegistrationBean<GrusFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(grusFilter);
        registration.addUrlPatterns("/*");
        registration.setName("grusFilter");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrusHealthChecker grusHealthChecker() {
        return new DefaultGrusHealthChecker();
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "grus.web.http2.enabled", havingValue = "true")
    public static class Http2AutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        Http2WebServerFactoryCustomizer http2WebServerFactoryCustomizer(WebProperties webProperties) {
            return new Http2WebServerFactoryCustomizer(webProperties.getHttp2());
        }
    }

}
