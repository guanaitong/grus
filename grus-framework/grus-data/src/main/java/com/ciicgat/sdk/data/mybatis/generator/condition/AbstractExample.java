/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import com.ciicgat.sdk.data.mybatis.generator.util.SqlUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 抽象查询示例
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class AbstractExample<T, R, Children extends AbstractExample<T, R, Children>> implements Example<T> {

    /**
     * 排序语句
     */
    private String orderByClause;

    /**
     * 是否去重
     */
    private boolean distinct;

    /**
     * 条件
     */
    private List<GeneratedCriteria<T, Children, R>> oredCriteria;

    /**
     * offset
     */
    private int limitStart;

    /**
     * limit
     */
    private int limitEnd;

    public AbstractExample() {
        oredCriteria = new ArrayList<>();
    }


    public String getOrderByClause() {
        return Objects.nonNull(orderByClause) ? orderByClause : "`id` DESC";
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<GeneratedCriteria<T, Children, R>> getOredCriteria() {
        return oredCriteria;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public int getLimitEnd() {
        return limitEnd;
    }

    @Override
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    @Override
    public void setLimitEnd(int limitEnd) {
        this.limitEnd = limitEnd;
    }

    @Override
    public Criteria<T> createCriteria() {
        Criteria<T> criteria = this.createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add((GeneratedCriteria) criteria);
        }
        return criteria;
    }

    @Override
    public LambdaCriteria<T> createLambdaCriteria() {
        LambdaCriteria<T> criteria = this.createLambdaCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add((GeneratedCriteria) criteria);
        }
        return criteria;
    }

    @Override
    public void or(GeneratedCriteria criteria) {
        oredCriteria.add(criteria);
    }

    @Override
    public Criteria<T> or() {
        Criteria<T> criteria = this.createCriteriaInternal();
        oredCriteria.add((GeneratedCriteria) criteria);
        return criteria;
    }

    @Override
    public LambdaCriteria<T> orLambdaCriteria() {
        LambdaCriteria<T> criteria = this.createLambdaCriteriaInternal();
        oredCriteria.add((GeneratedCriteria) criteria);
        return criteria;
    }

    @Override
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Override
    public GeneratedCriteria getExistCriteria() {
        if (oredCriteria.size() > 0) {
            return oredCriteria.get(0);
        }
        return this.createCriteria();
    }

    private Criteria<T> createCriteriaInternal() {
        return new Criteria<>();
    }

    private LambdaCriteria<T> createLambdaCriteriaInternal() {
        return new LambdaCriteria<>();
    }

    /**
     * 添加排序
     *
     * @param columnName 字段名
     * @param isAsc      是否正序
     */
    public void addOrderBy(String columnName, boolean isAsc) {
        SqlUtils.checkColumnName(columnName);
        if (Objects.isNull(this.orderByClause)) {
            this.orderByClause = StringUtils.EMPTY;
        }
        String direction = (isAsc ? SqlKeyword.ASC : SqlKeyword.DESC).getKeyword();
        String orderBy = String.format(", `%s` %s", columnName, direction);
        this.orderByClause += orderBy;
    }
}
