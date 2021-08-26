/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.grus.boot.autoconfigure.gconf.GconfAutoConfiguration;
import com.ciicgat.sdk.data.datasource.DataSourceBuilder;
import com.ciicgat.sdk.data.datasource.DataSourceFactory;
import com.ciicgat.sdk.gconf.ConfigCollectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


/**
 * Created by August.Zhou on 2019-04-03 15:07.
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DataSourceFactory.class, ConfigCollectionFactory.class})
@EnableConfigurationProperties(DbProperties.class)
@AutoConfigureAfter({GconfAutoConfiguration.class})
@AutoConfigureBefore({org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class GrusDataAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusDataAutoConfiguration.class);


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "grus.db", value = "read-write-separation", havingValue = "false", matchIfMissing = true)
    public static class DisableReadWriteSeparationAutoConfiguration {
        @Autowired
        private DbProperties dbProperties;

        @Bean(name = {"dataSource", "masterDataSource"})
        @Primary
        @ConditionalOnMissingBean(name = "masterDataSource")
        public DataSource masterDataSource() {
            return DataSourceBuilder
                    .newBuilder()
                    .setPreferSlave(false)
                    .setJdbcParams(dbProperties.getJdbcParams())
                    .setDataSourceExtParams(dbProperties.getDataSourceExtParams())
                    .newDataSource();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "grus.db", value = "read-write-separation", havingValue = "true", matchIfMissing = false)
    public static class EnableReadWriteSeparationAutoConfiguration {

        @Autowired
        private DbProperties dbProperties;

        public EnableReadWriteSeparationAutoConfiguration() {
            LOGGER.info("启动读写分离");
        }

        @Bean(name = {"dataSource", "masterDataSource"})
        @Primary
        @ConditionalOnMissingBean(name = "masterDataSource")
        public DataSource masterDataSource() {
            return DataSourceBuilder
                    .newBuilder()
                    .setPreferSlave(false)
                    .setJdbcParams(dbProperties.getJdbcParams())
                    .setDataSourceExtParams(dbProperties.getDataSourceExtParams())
                    .newDataSource();
        }

        @Bean
        @ConditionalOnMissingBean(name = "slaveDataSource")
        public DataSource slaveDataSource() {
            return DataSourceBuilder
                    .newBuilder()
                    .setPreferSlave(true)
                    .setJdbcParams(dbProperties.getJdbcParams())
                    .setDataSourceExtParams(dbProperties.getDataSourceExtParams())
                    .newDataSource();
        }

    }

}
