/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg;


import com.ciicgat.grus.generator.cg.db.ConnectionFactory;
import com.ciicgat.grus.generator.cg.db.MysqlTableIntrospect;
import com.ciicgat.grus.generator.cg.db.TableIntrospect;

import java.sql.Connection;

/**
 * @author Clive Yuan
 * @date 2020/10/28
 */
public class TableIntrospectTest {
    public static void main(String[] args) {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://mdb.servers.dev.ofc/robin_example?tinyInt1isBit=false";
        String root = "amy";
        String password = "1qazxsw@";
        Connection connection = ConnectionFactory.createConnection(driver, url, root, password);
        TableIntrospect tableIntrospect = new MysqlTableIntrospect(connection);
        // TableInfo tableInfo = tableIntrospect.introspect("Test");
        // System.out.println(tableInfo);
        System.out.println(tableIntrospect.getAllTables());
    }
}
