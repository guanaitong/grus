/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.encryption;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbEncryptStringTypeHandler extends BaseTypeHandler<String> {

    private final DbEncryptHelper dbEncryptHelper;

    public DbEncryptStringTypeHandler(DbEncryptHelper dbEncryptHelper) {
        this.dbEncryptHelper = dbEncryptHelper;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String param, JdbcType jdbcType) throws SQLException {
        if (param != null) {
            preparedStatement.setString(i, dbEncryptHelper.encrypt(param));
        }
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String originResult = resultSet.getString(columnName);
        return getDbEncrypt(originResult);
    }

    private String getDbEncrypt(String originResult) {
        return dbEncryptHelper.decrypt(originResult);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String originResult = resultSet.getString(columnIndex);
        return getDbEncrypt(originResult);
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String originResult = callableStatement.getString(columnIndex);
        return getDbEncrypt(originResult);
    }
}
