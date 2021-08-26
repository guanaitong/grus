/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Created by August.Zhou on 2019-03-29 15:17.
 */
@ConfigurationProperties(prefix = "grus.db")
public class DbProperties {
    /**
     * 是否使用从库做读写分离，默认否
     */
    private boolean readWriteSeparation = false;


    /**
     * jdbc的参数，比如useAffectedRows=true
     */
    private Map<String, String> jdbcParams;

    /**
     * datasource的扩展配置
     */
    private Map<String, String> dataSourceExtParams;

    public boolean isReadWriteSeparation() {
        return readWriteSeparation;
    }

    public void setReadWriteSeparation(boolean readWriteSeparation) {
        this.readWriteSeparation = readWriteSeparation;
    }

    public Map<String, String> getJdbcParams() {
        return jdbcParams;
    }

    public void setJdbcParams(Map<String, String> jdbcParams) {
        this.jdbcParams = jdbcParams;
    }

    public Map<String, String> getDataSourceExtParams() {
        return dataSourceExtParams;
    }

    public DbProperties setDataSourceExtParams(Map<String, String> dataSourceExtParams) {
        this.dataSourceExtParams = dataSourceExtParams;
        return this;
    }
}
