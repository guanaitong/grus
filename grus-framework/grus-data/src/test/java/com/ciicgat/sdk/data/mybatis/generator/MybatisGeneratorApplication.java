/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator;

import com.ciicgat.sdk.data.datasource.DataSourceBuilder;
import com.ciicgat.sdk.data.mybatis.SQLTelemetryInterceptor;
import com.ciicgat.sdk.util.system.EnvHook;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

/**
 * @author Clive Yuan
 * @date 2020/12/22
 */
@SpringBootApplication
@ComponentScan("com.ciicgat.sdk.data.mybatis.generator")
public class MybatisGeneratorApplication {

    static {
        EnvHook.setAppName("grus-demo");
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(MybatisGeneratorApplication.class)
                .build()
                .run(args);
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.newBuilder().setFromGconf(true).newDataSource();
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        // 加入SQL 语句执行拦截器
        factoryBean.setPlugins(new Interceptor[] {new SQLTelemetryInterceptor()});
        return factoryBean.getObject();
    }
}
