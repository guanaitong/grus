/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.datasource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-11-18 10:25.
 */
public class DataSourceBuilder {
    private static final String DEFAULT_GCONF_KEY = "datasource.json";
    /**
     * 需要连接的数据库名字，这个如果设置了，优先级别高于dataSourceConfig里的dbName
     */
    private String dbName;
    /**
     * jdbc的自定义配置
     */
    private Map<String, String> jdbcParams;
    /**
     * 连接池的EXT配置
     */
    private Map<String, String> dataSourceExtParams;

    /**
     * dataSourceConfig是否是从gconf中获取
     */
    private boolean fromGconf = true;

    /**
     * dataSourceConfig从gconf中获取时，gconf对应的key
     */
    private String fromGconfKey = DEFAULT_GCONF_KEY;
    /**
     * 数据库配置信息，如果设置了fromGconf，那么会从gconf中获取
     */
    private DataSourceConfig dataSourceConfig;
    /**
     * 是否优先使用slave。如果true时，会优先去找slave的机器，如果找不到slave，会退回使用master。
     * 主要用于读写分离的场景
     */
    private boolean preferSlave = false;

    private DataSourceBuilder() {
    }

    public static DataSourceBuilder newBuilder() {
        return new DataSourceBuilder();
    }

    public DataSourceBuilder setFromGconf(boolean fromGconf) {
        this.fromGconf = fromGconf;
        return this;
    }

    public DataSourceBuilder setFromGconfKey(String fromGconfKey) {
        this.fromGconfKey = fromGconfKey;
        return this;
    }

    public DataSourceBuilder setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public DataSourceBuilder setJdbcParams(Map<String, String> jdbcParams) {
        this.jdbcParams = jdbcParams;
        return this;
    }

    public DataSourceBuilder setDataSourceExtParams(Map<String, String> dataSourceExtParams) {
        this.dataSourceExtParams = dataSourceExtParams;
        return this;
    }

    public DataSourceBuilder setDataSourceConfig(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
        return this;
    }

    public DataSourceBuilder setPreferSlave(boolean preferSlave) {
        this.preferSlave = preferSlave;
        return this;
    }

    String getDbName() {
        return dbName;
    }

    Map<String, String> getJdbcParams() {
        return jdbcParams;
    }

    Map<String, String> getDataSourceExtParams() {
        return dataSourceExtParams;
    }

    boolean isFromGconf() {
        return fromGconf;
    }

    String getFromGconfKey() {
        return fromGconfKey;
    }

    DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    boolean isPreferSlave() {
        return preferSlave;
    }

    public DataSource newDataSource() {
        return DataSourceFactory.createDataSource(this);
    }
}
