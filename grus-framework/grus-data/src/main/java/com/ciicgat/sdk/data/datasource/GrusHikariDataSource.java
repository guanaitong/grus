/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.datasource;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

/**
 * Created by August.Zhou on 2019-07-03 17:18.
 */
public class GrusHikariDataSource extends HikariDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusHikariDataSource.class);

    private final DataSourceBuilder dataSourceBuilder;
    private final MysqlServer mysqlServer;

    public GrusHikariDataSource(HikariConfig configuration, DataSourceBuilder dataSourceBuilder, MysqlServer mysqlServer) {
        super(configuration);
        this.dataSourceBuilder = dataSourceBuilder;
        this.mysqlServer = mysqlServer;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return super.getConnection();
        } catch (SQLTransientConnectionException e) {
            if (e.getMessage().contains("Connection is not available")) {
                LOGGER.error("严重警报：应用无法获取新数据库连接", e);
                Alert.send("严重警报：应用无法获取新数据库连接", e);
                FrigateNotifier.sendMessageByAppName(NotifyChannel.SMS, Module.DB, "严重警报：应用无法获取新数据库连接", e);
            }
            throw e;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            return super.getConnection(username, password);
        } catch (SQLTransientConnectionException e) {
            if (e.getMessage().contains("Connection is not available")) {
                LOGGER.error("严重警报：应用无法获取新数据库连接", e);
                Alert.send("严重警报：应用无法获取新数据库连接", e);
                FrigateNotifier.sendMessageByAppName(NotifyChannel.SMS, Module.DB, "严重警报：应用无法获取新数据库连接", null);
            }
            throw e;
        } catch (SQLException e) {
            throw e;
        }
    }
}
