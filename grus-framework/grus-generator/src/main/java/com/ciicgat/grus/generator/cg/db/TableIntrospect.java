/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.db;

import java.util.List;

/**
 * 表信息探测器
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public interface TableIntrospect {

    /**
     * 探测表信息
     *
     * @param tableName 表名
     * @return
     */
    TableInfo introspect(String tableName);

    /**
     * 获取所有表名
     *
     * @return
     */
    List<String> getAllTables();

    /**
     * 关闭数据库连接
     */
    void closeConnection();
}
