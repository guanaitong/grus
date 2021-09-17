/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import java.io.Serializable;
import java.util.List;

/**
 * 实体
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class Entity implements Serializable {
    /**
     * 是否有BigDecimal类型的字段
     */
    private boolean hasBigDecimalField;
    /**
     * 小写驼峰名
     */
    private String lowerCamelName;
    /**
     * 实体名 (用户自定义, 若无则为首字母大写的驼峰)
     */
    private String entityName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 大写驼峰名
     */
    private String upperCamelName;
    /**
     * 备注
     */
    private String comment;
    /**
     * 字段
     */
    private List<Field> fields;

    // 内部使用字段
    /**
     * 文件名(生成文件用)
     */
    private String fileName;

    public boolean isHasBigDecimalField() {
        return hasBigDecimalField;
    }

    public Entity setHasBigDecimalField(boolean hasBigDecimalField) {
        this.hasBigDecimalField = hasBigDecimalField;
        return this;
    }

    public String getLowerCamelName() {
        return lowerCamelName;
    }

    public Entity setLowerCamelName(String lowerCamelName) {
        this.lowerCamelName = lowerCamelName;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public Entity setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public Entity setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getUpperCamelName() {
        return upperCamelName;
    }

    public Entity setUpperCamelName(String upperCamelName) {
        this.upperCamelName = upperCamelName;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Entity setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Entity setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public Entity setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
}
