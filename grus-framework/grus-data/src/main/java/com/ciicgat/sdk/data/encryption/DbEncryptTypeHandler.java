/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.encryption;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.CHAR})
@MappedTypes(EncryptField.class)
public class DbEncryptTypeHandler extends BaseTypeHandler<EncryptField> {

    private final DbEncryptHelper dbEncryptHelper;

    public DbEncryptTypeHandler(DbEncryptHelper dbEncryptHelper) {
        this.dbEncryptHelper = dbEncryptHelper;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, EncryptField param, JdbcType jdbcType) throws SQLException {
        if (param != null) {
            preparedStatement.setString(i, dbEncryptHelper.encrypt(param.getValue()));
        }
    }

    @Override
    public EncryptField getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String originResult = resultSet.getString(columnName);
        return getDbEncrypt(originResult);
    }

    private EncryptField getDbEncrypt(String originResult) {
        String decrypt = dbEncryptHelper.decrypt(originResult);
        if (decrypt != null) {
            return new EncryptField(decrypt);
        } else {
            return null;
        }
    }

    @Override
    public EncryptField getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String originResult = resultSet.getString(columnIndex);
        return getDbEncrypt(originResult);
    }

    @Override
    public EncryptField getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String originResult = callableStatement.getString(columnIndex);
        return getDbEncrypt(originResult);
    }
}
