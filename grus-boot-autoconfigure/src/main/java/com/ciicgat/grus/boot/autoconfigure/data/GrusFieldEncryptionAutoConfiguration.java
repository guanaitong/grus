/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;


import com.ciicgat.sdk.data.encryption.DbEncryptHelper;
import com.ciicgat.sdk.data.encryption.DbEncryptStringTypeHandler;
import com.ciicgat.sdk.data.encryption.DbEncryptTypeHandler;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.ClobTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.NClobTypeHandler;
import org.apache.ibatis.type.NStringTypeHandler;
import org.apache.ibatis.type.StringTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * Created by August.Zhou on 2019-06-25 16:21.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class, DbEncryptTypeHandler.class})
@EnableConfigurationProperties(DbProperties.class)
@AutoConfigureBefore(GrusMybatisAutoConfiguration.class)
public class GrusFieldEncryptionAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusFieldEncryptionAutoConfiguration.class);

    @Autowired
    private DbProperties dbProperties;

    @Bean
    @ConditionalOnMissingBean
    public DbEncryptHelper dbEncryptHelper() {
        Assert.state(StringUtils.isNotBlank(dbProperties.getFieldEncryption().getSecretKey()),
                "FieldEncryption SecretKey can't be empty when enable field encryption, please set grus.db.field-encryption-secret-key");
        return new DbEncryptHelper(dbProperties.getFieldEncryption().getSecretKey());
    }

    @Bean("dbEncryptConfigurationCustomizer")
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer(DbEncryptHelper dbEncryptHelper) {
        LOGGER.info("inject dbEncryptHelper");
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(new DbEncryptTypeHandler(dbEncryptHelper));
            configuration.getTypeHandlerRegistry().register(new DbEncryptStringTypeHandler(dbEncryptHelper));
            //DbEncryptStringTypeHandler 一定得通过注入方式放到mybatis的类中, 不然拿不到配置的 secretKey 信息
            //注册后, allTypeHandlersMap 中有该类型, mapper.xml 中的 typeHandler 就能识别到
            //但是 DbEncryptStringTypeHandler 与 StringTypeHandler 的支持类型完全一致, 且后注册的在 mybatis 中会优先使用
            //所以为了让大多数 String 字段都走默认的 StringTypeHandler, 仅设置了 typeHandler 的使用定制的 DbEncryptStringTypeHandler
            //需要重新 register 下 StringTypeHandler 覆盖 DbEncryptStringTypeHandler, 底层使用了 typeHandlerMap(Map) 存储了mybatis支持自动识别的类型, 会影响默认处理
            configuration.getTypeHandlerRegistry().register(String.class, new StringTypeHandler());
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.CHAR, new StringTypeHandler());
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.CLOB, new ClobTypeHandler());
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.LONGVARCHAR, new StringTypeHandler());
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.NVARCHAR, new NStringTypeHandler());
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.NCHAR, new NStringTypeHandler());
            configuration.getTypeHandlerRegistry().register(String.class, JdbcType.NCLOB, new NClobTypeHandler());
        };
    }

}
