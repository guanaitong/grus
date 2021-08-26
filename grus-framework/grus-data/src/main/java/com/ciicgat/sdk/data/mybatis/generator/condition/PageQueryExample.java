/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

/**
 * 分页查询示例
 *
 * @author Clive Yuan
 * @date 2020/10/30
 */
public class PageQueryExample<T> extends QueryExample<T> {
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NO = 1;

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 页号 (默认1)
     */
    private Integer page = DEFAULT_PAGE_NO;

    /**
     * 每页条数 (默认10)
     */
    private Integer rowsPerPage = DEFAULT_PAGE_SIZE;

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
}
