/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.ciicgat.grus.sentinel.SentinelDefaultConfig;
import com.ciicgat.grus.sentinel.ServerRuleManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2024/4/20 22:54
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({SentinelWebInterceptor.class, BlockExceptionHandler.class})
@EnableConfigurationProperties(GrusSentinelProperties.class)
public class GrusSentinelWebAutoConfiguration {

    private final GrusSentinelProperties grusSentinelProperties;

    public GrusSentinelWebAutoConfiguration(GrusSentinelProperties grusSentinelProperties) {
        this.grusSentinelProperties = grusSentinelProperties;
        GrusSentinelProperties.SystemRule systemRule = grusSentinelProperties.getSystem();
        if (systemRule.isEnabled()) {
            ServerRuleManager.loadSystemRule(systemRule.getMaxThread(), systemRule.getAvgRt());
        }
        SentinelDefaultConfig.CLIENT_DEFAULT_FLOW = grusSentinelProperties.getClient().getFlow();
        SentinelDefaultConfig.CLIENT_DEFAULT_DEGRADE = grusSentinelProperties.getClient().getDegrade();
    }

    @Bean
    @ConditionalOnMissingBean
    public BlockExceptionHandler blockExceptionHandler() {
        return new GrusBlockExceptionHandler();
    }
}
