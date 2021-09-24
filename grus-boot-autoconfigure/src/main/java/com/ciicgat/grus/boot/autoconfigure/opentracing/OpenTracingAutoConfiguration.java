/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentracing;

import com.ciicgat.grus.boot.autoconfigure.condition.ConditionalOnServerEnv;
import com.ciicgat.sdk.data.mybatis.SQLLineInterceptor;
import com.ciicgat.sdk.data.mybatis.SQLTracingInterceptor;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerFactory;
import io.opentracing.util.GlobalTracer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Created by August.Zhou on 2019-03-25 14:55.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({TracerFactory.class, JaegerTracer.class})
@ConditionalOnProperty(prefix = "grus.opentracing", value = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnServerEnv
public class OpenTracingAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTracingAutoConfiguration.class);


    @Bean
    @ConditionalOnMissingBean
    public TracerFactory tracerFactory() {
        return new GconfTracerFactory();
    }

    @Bean
    public Tracer tracer(TracerFactory tracerFactory) {
        GlobalTracer.registerIfAbsent(tracerFactory::getTracer);
        return GlobalTracer.get();
    }


    @ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class, SQLLineInterceptor.class, SQLTracingInterceptor.class})
    @Configuration(proxyBeanMethods = false)
    public static class SQLTracingInterceptorConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @Order
        public SQLTracingInterceptor sqlTracingInterceptor() {
            return new SQLTracingInterceptor();
        }
    }


}
