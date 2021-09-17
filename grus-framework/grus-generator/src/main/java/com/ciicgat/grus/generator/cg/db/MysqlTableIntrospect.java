/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.db;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MySQL 表信息探测器
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public class MysqlTableIntrospect implements TableIntrospect {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlTableIntrospect.class);
    private final Connection connection;

    public MysqlTableIntrospect(Connection connection) {
        this.connection = connection;
    }

    @Override
    public TableInfo introspect(String tableName) {
        try {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setName(tableName);
            DatabaseMetaData metaData = connection.getMetaData();
            // introspect table info
            try (ResultSet tableRs = metaData.getTables(connection.getCatalog(), null, tableName, null)) {
                if (tableRs.next()) {
                    String remarks = tableRs.getString("REMARKS");
                    if (StringUtils.isBlank(remarks)) {
                        remarks = tableName;
                    }
                    tableInfo.setComment(remarks);
                }
            }
            List<ColumnInfo> columns = Lists.newArrayList();
            tableInfo.setColumns(columns);

            // introspect column info
            try (ResultSet columnRs = metaData.getColumns(connection.getCatalog(), null, tableName, null)) {
                while (columnRs.next()) {
                    String columnName = columnRs.getString("COLUMN_NAME");
                    String typeName = columnRs.getString("TYPE_NAME");
                    int columnSize = columnRs.getInt("COLUMN_SIZE");
                    int nullable = columnRs.getInt("NULLABLE");
                    String comment = columnRs.getString("REMARKS");
                    ColumnInfo column = new ColumnInfo();
                    column.setName(columnName);
                    column.setType(typeName);
                    column.setLength(columnSize);
                    column.setNullable(nullable == 1);
                    column.setComment(StringUtils.isNotBlank(comment) ? comment : columnName);
                    columns.add(column);
                }
            }
            return tableInfo;
        } catch (SQLException e) {
            LOGGER.error("introspect", e);
            throw new RuntimeException("Fail to introspect table: " + tableName);
        }
    }

    @Override
    public List<String> getAllTables() {
        List<String> list = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tableRs = metaData.getTables(connection.getCatalog(), null, null, null);
            while (tableRs.next()) {
                list.add(tableRs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            LOGGER.error("introspect", e);
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void closeConnection() {
        if (Objects.nonNull(connection)) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
