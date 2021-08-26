/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;



import com.ciicgat.grus.json.JSON;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 分页信息
 * <p>
 * Created by August.Zhou on 2018-10-22 14:02.
 */
public class PageInfo<T> implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;


    //-- 公共变量 --//
    public static final String ASC = "asc";
    public static final String DESC = "desc";

    //-- 分页参数 --//
    /**
     * 当前页，从1开始
     */
    private final int page;
    /**
     * 每页的条数
     */
    private final int perPage;
    /**
     * 当前页起始的行坐标
     */
    private final int start;

    /**
     * 当前页结束的行坐标
     */
    private final int end;

    private String orderBy = null;
    private String order = null;

    //-- 返回结果 --//
    private List<T> list = new ArrayList<>();
    private int total = -1;

    //-- 构造函数 --//
    public PageInfo() {
        this(1, 25);
    }

    public PageInfo(int page, int perPage) {
        this.page = page;
        this.perPage = perPage;

        this.start = (page - 1) * perPage;
        this.end = page * perPage;
    }


    public final int getPage() {
        return page;
    }


    public final int getPerPage() {
        return perPage;
    }

    public final int getStart() {
        return start;
    }

    public final int getEnd() {
        return end;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
        }
        PageInfo pageInfo = new PageInfo(this.page, this.perPage);
        pageInfo.setOrderBy(this.orderBy);
        pageInfo.setOrder(this.order);
        pageInfo.setTotal(this.total);
        pageInfo.setList(this.list);
        return pageInfo;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageInfo)) return false;
        PageInfo<?> pageInfo = (PageInfo<?>) o;
        return page == pageInfo.page &&
                perPage == pageInfo.perPage &&
                start == pageInfo.start &&
                end == pageInfo.end &&
                total == pageInfo.total &&
                Objects.equals(orderBy, pageInfo.orderBy) &&
                Objects.equals(order, pageInfo.order) &&
                Objects.equals(list, pageInfo.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, perPage, start, end, orderBy, order, list, total);
    }
}
