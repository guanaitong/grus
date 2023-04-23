/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.rabbitmq.client.Connection;

import java.util.Properties;

/**
 * Created by August.Zhou on 2022/7/7 17:11
 */
public class TestConnection {


    public static Connection getConnection() {
        Properties properties = GlobalGconfConfig.getConfig().getProperties("rabbitmq.properties");
        var user = properties.getProperty("mq.default.username");
        var pwd = properties.getProperty("mq.default.pwd");
        Connection connection = ConnectionFactory.newConnectionFromCache("mq.servers.dev.ofc", 5672, user, pwd, "/", 4);
        return connection;
    }
}
