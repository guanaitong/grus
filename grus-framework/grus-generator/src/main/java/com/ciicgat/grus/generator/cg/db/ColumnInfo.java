/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.db;


/**
 * 列信息
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public class ColumnInfo {
    /**
     * 列名
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 长度
     */
    private Integer length;
    /**
     * 能否为空
     */
    private Boolean nullable;
    /**
     * 备注
     */
    private String comment;

    public String getName() {
        return name;
    }

    public ColumnInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public ColumnInfo setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public ColumnInfo setLength(Integer length) {
        this.length = length;
        return this;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public ColumnInfo setNullable(Boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ColumnInfo setComment(String comment) {
        this.comment = comment;
        return this;
    }
}
