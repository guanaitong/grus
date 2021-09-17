/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 连接工厂
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private static final String QUESTION_MARK = "?";
    private static final String AND_MARK = "&";
    private static final String URL_PARAMS = "useInformationSchema=true";

    /**
     * 创建数据库连接
     *
     * @param driver 驱动类
     * @param url 连接
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static Connection createConnection(String driver, String url, String username, String password) {
        try {
            Class.forName(driver);
            if (url.contains(QUESTION_MARK)) {
                url += AND_MARK.concat(URL_PARAMS);
            } else {
                url += QUESTION_MARK.concat(URL_PARAMS);
            }
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("createConnection", e);
            throw new RuntimeException(String.format("Fail to create connection with driver=%s,url=%s,username=%s,password=%s", driver, url, username, password)); //NOSONAR
        }
    }

}
