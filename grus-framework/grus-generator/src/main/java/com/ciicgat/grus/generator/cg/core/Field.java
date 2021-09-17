/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import com.ciicgat.grus.generator.cg.db.JdbcType;

import java.io.Serializable;

/**
 * 字段
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class Field implements Serializable {
    /**
     * JDBC类型
     */
    private JdbcType jdbcType;
    /**
     * 小写驼峰名
     */
    private String lowerCamelName;
    /**
     * 大写驼峰名
     */
    private String upperCamelName;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 长度
     */
    private Integer length;
    /**
     * 是否可空
     */
    private Boolean nullable;
    /**
     * 备注
     */
    private String comment;
    /**
     * 是否标注字段名
     * <p>当lowerCamelName与columnName不一致时需要标注</p>
     */
    private boolean markColumnName;
    /**
     * 是否为主键
     */
    private boolean primaryKey;
    /**
     * 保存忽略
     */
    private boolean ignoreSaving;
    /**
     * swagger 字段隐藏
     */
    private boolean swaggerHidden;

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public Field setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }

    public String getLowerCamelName() {
        return lowerCamelName;
    }

    public Field setLowerCamelName(String lowerCamelName) {
        this.lowerCamelName = lowerCamelName;
        return this;
    }

    public String getUpperCamelName() {
        return upperCamelName;
    }

    public Field setUpperCamelName(String upperCamelName) {
        this.upperCamelName = upperCamelName;
        return this;
    }

    public String getColumnName() {
        return columnName;
    }

    public Field setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public Field setLength(Integer length) {
        this.length = length;
        return this;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public Field setNullable(Boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Field setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public boolean isMarkColumnName() {
        return markColumnName;
    }

    public Field setMarkColumnName(boolean markColumnName) {
        this.markColumnName = markColumnName;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public Field setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public boolean isIgnoreSaving() {
        return ignoreSaving;
    }

    public Field setIgnoreSaving(boolean ignoreSaving) {
        this.ignoreSaving = ignoreSaving;
        return this;
    }

    public boolean isSwaggerHidden() {
        return swaggerHidden;
    }

    public Field setSwaggerHidden(boolean swaggerHidden) {
        this.swaggerHidden = swaggerHidden;
        return this;
    }
}
