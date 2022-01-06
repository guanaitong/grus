/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import com.ciicgat.sdk.data.mybatis.generator.util.SqlUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 条件明细
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class Criterion {

    private final String property;
    private final String condition;
    private final SqlKeyword sqlKeyword;
    private final Object value;
    private Object secondValue;
    private boolean noValue;
    private boolean singleValue;
    private boolean likeValue;
    private boolean betweenValue;
    private boolean listValue;
    private SqlLike sqlLike;

    private Criterion(String property, Object value, SqlKeyword sqlKeyword) {
        Assert.notNull(property, "property is required");
        Assert.notNull(sqlKeyword, "sqlKeyword is required");
        this.property = property;
        this.value = value;
        this.sqlKeyword = sqlKeyword;
        this.condition = parseCondition();
    }

    private String parseCondition() {
        return SqlUtils.contactCondition(property, sqlKeyword);
    }

    protected static Criterion build(String property, Object value, SqlKeyword sqlKeyword) {
        Criterion criterion = new Criterion(property, value, sqlKeyword);
        if (value instanceof List<?>) {
            criterion.listValue = true;
        } else {
            criterion.singleValue = true;
        }
        return criterion;
    }

    protected static Criterion buildNoValue(String property, SqlKeyword sqlKeyword) {
        Criterion criterion = build(property, null, sqlKeyword);
        criterion.noValue = true;
        return criterion;
    }

    protected static Criterion buildLike(String property, Object value, SqlLike sqlLike, SqlKeyword sqlKeyword) {
        Criterion criterion = build(property, value, sqlKeyword);
        criterion.sqlLike = sqlLike;
        criterion.likeValue = true;
        return criterion;
    }

    protected static Criterion buildBetween(String property, Object value, Object secondValue, SqlKeyword sqlKeyword) {
        Criterion criterion = build(property, value, sqlKeyword);
        criterion.secondValue = secondValue;
        criterion.betweenValue = true;
        return criterion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Criterion criterion = (Criterion) o;

        return new EqualsBuilder().append(noValue, criterion.noValue).append(singleValue, criterion.singleValue).append(likeValue, criterion.likeValue).append(betweenValue, criterion.betweenValue).append(listValue, criterion.listValue).append(property, criterion.property).append(value, criterion.value).append(secondValue, criterion.secondValue).append(sqlLike, criterion.sqlLike).append(sqlKeyword, criterion.sqlKeyword).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(property).append(value).append(secondValue).append(noValue).append(singleValue).append(likeValue).append(betweenValue).append(listValue).append(sqlLike).append(sqlKeyword).toHashCode();
    }

    public String getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }

    public Object getSecondValue() {
        return secondValue;
    }

    public boolean isNoValue() {
        return noValue;
    }

    public boolean isSingleValue() {
        return singleValue;
    }

    public boolean isBetweenValue() {
        return betweenValue;
    }

    public boolean isListValue() {
        return listValue;
    }

    public boolean isLikeValue() {
        return likeValue;
    }

    public SqlLike getSqlLike() {
        return sqlLike;
    }

    public SqlKeyword getSqlKeyword() {
        return sqlKeyword;
    }

    public String getCondition() {
        return condition;
    }
}
