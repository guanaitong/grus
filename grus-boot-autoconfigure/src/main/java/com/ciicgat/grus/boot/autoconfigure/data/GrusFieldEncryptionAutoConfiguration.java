/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;


import com.ciicgat.sdk.data.encryption.DbEncryptHelper;
import com.ciicgat.sdk.data.encryption.DbEncryptTypeHandler;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
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
        return configuration -> configuration.getTypeHandlerRegistry().register(new DbEncryptTypeHandler(dbEncryptHelper));
    }

}
