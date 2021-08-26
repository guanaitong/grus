/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.datasource;

import com.ciicgat.sdk.util.system.Systems;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Created by August.Zhou on 2019-06-12 16:08.
 */
public class TestDataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataSourceFactory.class);

    public static DataSource createDataSource(final String driverClassName, String jdbcUrl) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(driverClassName);
        LOGGER.info("create datasource use driver {} jdbcUrl {}", driverClassName, jdbcUrl);
        config.setJdbcUrl(jdbcUrl);
        config.setPoolName("HikariCP Pool of " + Systems.APP_NAME);

        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cachePrepStmts", "true");

//        DataSourceFactory.setDataSourceConfig(config, new HashMap<>());

        HikariDataSource hikariDataSource = new HikariDataSource(config);
        return hikariDataSource;

    }
}
