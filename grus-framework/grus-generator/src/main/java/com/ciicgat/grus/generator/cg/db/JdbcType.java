/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.db;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 类型映射
 *
 * <a href="https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html">doc</a>
 *
 * @author Clive Yuan
 * @date 2020/11/04
 */
public enum JdbcType {

    BOOLEAN(Boolean.class, "BOOL", "BOOLEAN"),
    BYTE_ARRAY(byte[].class, "BIT", "BINARY", "VARBINARY", "TINYBLOB", "BLOB", "MEDIUMBLOB", "LONGBLOB"),
    INTEGER(Integer.class, "TINYINT", "SMALLINT", "MEDIUMINT", "INT", "INTEGER"),
    LONG(Long.class, "BIGINT"),
    DOUBLE(Double.class, "DOUBLE"),
    FLOAT(Float.class, "FLOAT"),
    DECIMAL(BigDecimal.class, "DECIMAL"),
    DATE(Date.class, "DATE", "DATETIME", "TIMESTAMP", "TIME", "YEAR"),
    STRING(String.class, "ENUM", "SET", "CHAR", "VARCHAR", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT"),
    OBJECT(Object.class, "");


    JdbcType(Class<?> javaType, String... sqlTypes) {
        this.javaType = javaType;
        this.sqlTypes = sqlTypes;
    }

    /**
     * Java类型
     */
    private final Class<?> javaType;
    /**
     * 数据库字段类型
     */
    private final String[] sqlTypes;

    public Class<?> getJavaType() {
        return javaType;
    }

    public String[] getSqlTypes() {
        return sqlTypes;
    }
}
