/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.template;

/**
 * 分页查询
 *
 * @author Clive Yuan
 * @date 2020/12/07
 */
public class PageQueryRequest<T> {
    /**
     * 页号
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer rowsPerPage;

    /**
     * 实体
     */
    private T entity;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "PageQueryRequest{" +
                "page=" + page +
                ", rowsPerPage=" + rowsPerPage +
                ", entity=" + entity +
                '}';
    }
}
