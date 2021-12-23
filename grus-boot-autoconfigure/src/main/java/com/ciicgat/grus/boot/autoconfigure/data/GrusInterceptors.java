/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.sdk.data.mybatis.SQLLineInterceptor;
import com.ciicgat.sdk.data.mybatis.SQLTracingInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Created by August.Zhou on 2019-06-25 16:21.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class, SQLLineInterceptor.class})
public class GrusInterceptors {

    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SQLLineInterceptor sqlLineInterceptor() {
        return new SQLLineInterceptor();
    }


    @Bean
    @ConditionalOnMissingBean
    @Order
    public SQLTracingInterceptor sqlTracingInterceptor() {
        return new SQLTracingInterceptor();
    }

}
