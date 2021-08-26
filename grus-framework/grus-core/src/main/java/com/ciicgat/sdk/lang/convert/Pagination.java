/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import java.io.Serializable;
import java.util.List;

/**
 * Created by August.Zhou on 2017/7/27 18:05.
 */
public class Pagination<T> implements Serializable {

    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 200;
    private static final long serialVersionUID = 1L;
    /**
     * <strong>属性名不要更改</strong>
     */
    private int totalCount;

    /**
     * <strong>属性名不要更改</strong>
     */
    private List<T> dataList;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    public Pagination() {

    }

    public Pagination(int totalCount, List<T> dataList) {
        this.totalCount = totalCount;
        this.dataList = dataList;
    }

    public Pagination(int totalCount, List<T> dataList, int curPage, int pageSize) {
        this.totalCount = totalCount;
        this.dataList = dataList;
        int pageNum = (totalCount / pageSize) + ((totalCount % pageSize == 0) ? 0 : 1);
        this.hasNext = curPage < pageNum;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pagination)) return false;

        Pagination<?> that = (Pagination<?>) o;

        if (totalCount != that.totalCount) return false;
        if (hasNext != that.hasNext) return false;
        return dataList != null ? dataList.equals(that.dataList) : that.dataList == null;
    }

    @Override
    public int hashCode() {
        int result = totalCount;
        result = 31 * result + (dataList != null ? dataList.hashCode() : 0);
        result = 31 * result + (hasNext ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "totalCount=" + totalCount +
                ", dataList=" + dataList +
                ", hasNext=" + hasNext +
                '}';
    }
}
