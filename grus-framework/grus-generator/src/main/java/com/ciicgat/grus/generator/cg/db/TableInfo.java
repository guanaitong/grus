/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.db;

import java.util.List;

/**
 * 表信息
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public class TableInfo {
    /**
     * 名称
     */
    private String name;
    /**
     * 备注
     */
    private String comment;
    /**
     * 字段列表
     */
    private List<ColumnInfo> columns;

    public String getName() {
        return name;
    }

    public TableInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public TableInfo setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public TableInfo setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
        return this;
    }
}
