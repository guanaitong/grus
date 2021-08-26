/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.datasource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-04-15 13:11.
 */
public class DataSourceConfig {


    /**
     * 需要连接的数据库名字
     */
    private String dbName;

    /**
     * 这个是连接池的MXBEAN配置，不是jdbc的配置。
     */
    private Map<String, String> params = Collections.emptyMap();

    private String username;

    private String encryptedPassword;

    private String password;

    private int maxUserConnections;

    private String groupName;

    private List<MysqlServer> mysqlServers;


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxUserConnections() {
        return maxUserConnections;
    }

    public void setMaxUserConnections(int maxUserConnections) {
        this.maxUserConnections = maxUserConnections;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<MysqlServer> getMysqlServers() {
        return mysqlServers;
    }

    public void setMysqlServers(List<MysqlServer> mysqlServers) {
        this.mysqlServers = mysqlServers;
    }
}
