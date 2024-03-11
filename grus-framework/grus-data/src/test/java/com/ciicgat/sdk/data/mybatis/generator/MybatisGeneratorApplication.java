/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator;

import com.ciicgat.sdk.data.datasource.DataSourceBuilder;
import com.ciicgat.sdk.data.encryption.DbEncryptHelper;
import com.ciicgat.sdk.data.encryption.DbEncryptStringTypeHandler;
import com.ciicgat.sdk.data.encryption.DbEncryptTypeHandler;
import com.ciicgat.sdk.data.mybatis.SQLTracingInterceptor;
import com.ciicgat.sdk.util.system.EnvHook;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.ClobTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.NClobTypeHandler;
import org.apache.ibatis.type.NStringTypeHandler;
import org.apache.ibatis.type.StringTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @author Clive Yuan
 * @date 2020/12/22
 */
@SpringBootApplication(scanBasePackages = "com.ciicgat.sdk.data.mybatis.generator.mapper")
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
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource,
                                                  MybatisProperties mybatisProperties) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        // 加入SQL 语句执行拦截器
        factoryBean.setPlugins(new Interceptor[]{new SQLTracingInterceptor()});
        factoryBean.setMapperLocations(mybatisProperties.resolveMapperLocations());
        Configuration configuration = new Configuration();
        DbEncryptHelper dbEncryptHelper = new DbEncryptHelper("KRmAo5c6uavF7fYQ");
        configuration.getTypeHandlerRegistry().register(new DbEncryptTypeHandler(dbEncryptHelper));
        configuration.getTypeHandlerRegistry().register(new DbEncryptStringTypeHandler(dbEncryptHelper));

        configuration.getTypeHandlerRegistry().register(String.class, new StringTypeHandler());
        configuration.getTypeHandlerRegistry().register(String.class, JdbcType.CHAR, new StringTypeHandler());
        configuration.getTypeHandlerRegistry().register(String.class, JdbcType.CLOB, new ClobTypeHandler());
        configuration.getTypeHandlerRegistry().register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
        configuration.getTypeHandlerRegistry().register(String.class, JdbcType.LONGVARCHAR, new StringTypeHandler());
        configuration.getTypeHandlerRegistry().register(String.class, JdbcType.NVARCHAR, new NStringTypeHandler());
        configuration.getTypeHandlerRegistry().register(String.class, JdbcType.NCHAR, new NStringTypeHandler());
        configuration.getTypeHandlerRegistry().register(String.class, JdbcType.NCLOB, new NClobTypeHandler());
        factoryBean.setConfiguration(configuration);
        return factoryBean.getObject();
    }


}
