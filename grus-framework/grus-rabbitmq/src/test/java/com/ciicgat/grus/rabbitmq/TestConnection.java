/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.rabbitmq.client.Connection;

/**
 * Created by August.Zhou on 2022/7/7 17:11
 */
public class TestConnection {


    public static Connection getConnection() {
        return ConnectionFactory.newConnectionFromCache("mq.servers.dev.ofc:5672",  "mq_mgr", "gYul86Ci7BNcHfhxG36aMa3E3g0=", "/", 4);
    }
}
