/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.datasource;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.data.connection.JdbcUrlBuilder;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.gconf.security.PublicKeyOwner;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkRegion;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariConfigMXBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Created by August.Zhou on 2019-04-15 13:39.
 */
public class DataSourceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);
    private static ConfigCollection configCollection;

    static {
        configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection();
        Objects.requireNonNull(configCollection, "应用[" + Systems.APP_NAME + "]不存在");
    }


    public static DataSource createDataSource(DataSourceBuilder dataSourceBuilder) {
        if (dataSourceBuilder.isFromGconf()) {
            if (StringUtils.isEmpty(dataSourceBuilder.getFromGconfKey())) {
                throw new IllegalArgumentException("使用gconf获取配置时，请指定gconfkey");
            }
            dataSourceBuilder.setDataSourceConfig(configCollection.getBean(dataSourceBuilder.getFromGconfKey(), DataSourceConfig.class));
        }
        DataSourceConfig dataSourceConfig = dataSourceBuilder.getDataSourceConfig();
        // 优先使用DataSourceBuilder传过来的dbName，如果为空，那么使用dataSourceConfig的dbName，同时设置dataSourceBuilder
        if (StringUtils.isEmpty(dataSourceBuilder.getDbName())) {
            dataSourceBuilder.setDbName(dataSourceConfig.getDbName());
        }
        final MysqlServer mysqlServer = getMysqlServer(dataSourceConfig, dataSourceBuilder.isPreferSlave());

        GrusHikariDataSource dataSource = createDataSource(dataSourceBuilder, mysqlServer);

        if (dataSourceBuilder.isFromGconf()) {
            configCollection.addConfigChangeListener(dataSourceBuilder.getFromGconfKey(),
                    (key, oldValue, newValue) -> setDataSourceConfig(dataSource, JSON.parse(newValue, DataSourceConfig.class).getParams()));
        }

        return dataSource;
    }

    private static MysqlServer getMysqlServer(final DataSourceConfig dataSourceConfig, final boolean preferSlave) {
        MysqlServer masterServer = null;
        MysqlServer slaveServer = null;
        for (MysqlServer mysqlServer : dataSourceConfig.getMysqlServers()) {
            if ("master".equalsIgnoreCase(mysqlServer.getRole())) {
                masterServer = mysqlServer;
            } else if ("slave".equalsIgnoreCase(mysqlServer.getRole())) {
                slaveServer = mysqlServer;
            }
        }
        if (preferSlave && (slaveServer != null)) {
            return slaveServer;
        }
        return Objects.requireNonNull(masterServer, "机器组里没有master机器");
    }

    private static GrusHikariDataSource createDataSource(DataSourceBuilder dataSourceBuilder, MysqlServer mysqlServer) {
        DataSourceConfig dataSourceConfig = dataSourceBuilder.getDataSourceConfig();
        final String finalUsedDbName = dataSourceBuilder.getDbName();

        HikariConfig config = new HikariConfig();
        // Properties NOT changeable at runtime
        //
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        String jdbcUrl = buildJdbcUrl(finalUsedDbName, mysqlServer, dataSourceBuilder.getJdbcParams());
        LOGGER.info("create datasource use jdbcUrl {}", jdbcUrl);
        config.setJdbcUrl(jdbcUrl);
        config.setPoolName(dataSourceBuilder.isPreferSlave() ? "slaveDS" : "masterDS");


        config.setUsername(dataSourceConfig.getUsername());
        if (StringUtils.isNoneBlank(dataSourceConfig.getPassword()) && WorkRegion.getCurrentWorkRegion().isDevelop()) {
            config.setPassword(dataSourceConfig.getPassword());
        } else {
            try {
                config.setPassword(PublicKeyOwner.decrypt(dataSourceConfig.getEncryptedPassword()));
            } catch (Exception e) {
                LOGGER.error("密码错误，无法解密:" + dataSourceConfig.getEncryptedPassword(), e);
                throw new RuntimeException(e);
            }
        }


        // Properties changeable at runtime through the HikariConfigMXBean
        //
        setDataSourceConfig(config, dataSourceConfig.getParams());

        // Ext Properties
        //
        Map<String, String> dataSourceExtParams = dataSourceBuilder.getDataSourceExtParams();
        if (dataSourceExtParams != null) {
            for (Map.Entry<String, String> entry : dataSourceExtParams.entrySet()) {
                config.addDataSourceProperty(entry.getKey(), entry.getValue());
            }
        }

        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cachePrepStmts", "true");

        //对于JDBC4.0以后，只要HikariDataSource能够new成功，那么就说明全部配置OK
        GrusHikariDataSource hikariDataSource = new GrusHikariDataSource(config, dataSourceBuilder, mysqlServer);
        return hikariDataSource;

    }

    private static void setDataSourceConfig(HikariConfigMXBean hikariConfigMXBean, Map<String, String> mxParams) {
        if (mxParams == null) {
            mxParams = Collections.emptyMap();
        }
        hikariConfigMXBean.setMaximumPoolSize(getInt(mxParams, "maximumPoolSize", 30));
        hikariConfigMXBean.setMinimumIdle(getInt(mxParams, "minimumIdle", 1));
        hikariConfigMXBean.setConnectionTimeout(getLong(mxParams, "connectionTimeout", 3_000L));
        hikariConfigMXBean.setIdleTimeout(getLong(mxParams, "idleTimeout", 30_000L));
        hikariConfigMXBean.setMaxLifetime(getLong(mxParams, "maxLifetime", 300_000L));
        hikariConfigMXBean.setValidationTimeout(getLong(mxParams, "validationTimeout", 5_000L));
        hikariConfigMXBean.setLeakDetectionThreshold(getLong(mxParams, "leakDetectionThreshold", 60_000L));
    }


    private static String buildJdbcUrl(final String dbName, MysqlServer mysqlServer, Map<String, String> jdbcParams) {
        //优先使用域名，域名没有设置时使用ip
        final String host = StringUtils.isBlank(mysqlServer.getDomain()) ? mysqlServer.getIp() : mysqlServer.getDomain();
        final String port = StringUtils.isBlank(mysqlServer.getPort()) ? "3306" : mysqlServer.getPort();
        return JdbcUrlBuilder.build(host, port, dbName, jdbcParams);
    }

    private static int getInt(Map<String, String> params, String key, int defaultValue) {
        String s = params.get(key);
        if (StringUtils.isNotEmpty(s)) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                LOGGER.warn("error", e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private static long getLong(Map<String, String> params, String key, long defaultValue) {
        String s = params.get(key);
        if (StringUtils.isNotEmpty(s)) {
            try {
                return Long.parseLong(s);
            } catch (Exception e) {
                LOGGER.warn("error", e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

}
