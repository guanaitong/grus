/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.datasource;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.util.system.EnvHook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-04-15 15:46.
 */
public class DataSourcesTest {


    @Test
    public void testGenerateDataSourceConfig() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbName("userdoor");
        dataSourceConfig.setGroupName("GSDEV-NORMAL-MYSQL");
        dataSourceConfig.setMaxUserConnections(111);

        Map<String, String> params = new HashMap<>();
        dataSourceConfig.setParams(params);
        params.put("maximumPoolSize", "50");
        params.put("connectionTimeout", "4000");

        dataSourceConfig.setUsername("amy");
        dataSourceConfig.setEncryptedPassword("EeAkDjbrIwkCBLQr/I98iF9V3bJb2ixLXAX7nPReO664rCPa+lALlA9VJOH86g0yDQ3Lb9zwO9zqcMlpTRlVE7lrnw4eBQMz4VljucJ6h4GM6Vkr4C4Kg64vhUbOIlEhGX126vSeuq6gvWaBtAiyACZRDBrQaWBIghWuPoze/oK6tMJkenUwK6o+K6XVKG3jh/50TzWxWGCWK+4nJV0aoutZV6nRbOP75C5Rdtcg0btZ8PZY6JNT3Q8b0lAuvUg6ZIstEdYDS4yu/BcIIjHpfjslPTilTy9QSbzT5yMIldkZx5Ve0/sJhBtEtZQrMot9Chfsgp1D0TA8u2lYaG5+Yg==");

        MysqlServer masterServer = new MysqlServer();
        masterServer.setDomain("mdb.servers.dev.ofc");
        masterServer.setName("GSDEV-NORMAL-MYSQL-01");
        masterServer.setRole("master");

        List<MysqlServer> mysqlServers = new ArrayList<>();
        mysqlServers.add(masterServer);

        dataSourceConfig.setMysqlServers(mysqlServers);

        System.out.println(JSON.toJSONString(dataSourceConfig));
    }

    @Test
    public void testCreate() throws SQLException {
        EnvHook.setAppName("grus-demo");

        DataSource masterDataSource = DataSourceBuilder.newBuilder().setDbName("userdoor").newDataSource();

        DataSource slaveDataSource = DataSourceBuilder.newBuilder().setDbName("userdoor").setPreferSlave(true).newDataSource();

        Assertions.assertNotNull(masterDataSource.getConnection());
        Assertions.assertNotNull(slaveDataSource.getConnection());

    }

//    @Test
//    public void testCreate2() throws SQLException {
//        EnvPrepare.put("APP_NAME", "grus-demo");
//        DataSource masterDataSource = DataSourceFactory.createMasterDataSource("userdoor");
//
//
//        Assertions.assertNotNull(masterDataSource.getConnection());
//        Assertions.assertNotNull(masterDataSource.getConnection());
//        Assertions.assertNotNull(masterDataSource.getConnection());
//
//    }
}
