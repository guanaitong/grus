/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import java.util.List;
import java.util.Objects;

/**
 * 条件明细
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class Criterion {

    private String condition;

    private Object value;

    private Object secondValue;

    private boolean noValue;

    private boolean singleValue;

    private boolean likeValue;

    private boolean betweenValue;

    private boolean listValue;

    private String typeHandler;

    private SqlLike sqlLike;

    public String getCondition() {
        return condition;
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

    public String getTypeHandler() {
        return typeHandler;
    }

    public boolean isLikeValue() {
        return likeValue;
    }

    public SqlLike getSqlLike() {
        return sqlLike;
    }

    protected Criterion(String condition) {
        super();
        this.condition = condition;
        this.typeHandler = null;
        this.noValue = true;
    }

    protected Criterion(String condition, Object value, String typeHandler) {
        this(condition, value, typeHandler, (SqlLike) null);
    }

    protected Criterion(String condition, Object value, String typeHandler, SqlLike sqlLike) {
        super();
        this.condition = condition;
        this.value = value;
        this.typeHandler = typeHandler;
        if (Objects.nonNull(sqlLike)) {
            this.likeValue = true;
            this.sqlLike = sqlLike;
        } else {
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }
    }

    protected Criterion(String condition, Object value) {
        this(condition, value, (String) null);
    }

    protected Criterion(String condition, Object value, SqlLike sqlLike) {
        this(condition, value, null, sqlLike);
    }

    protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
        super();
        this.condition = condition;
        this.value = value;
        this.secondValue = secondValue;
        this.typeHandler = typeHandler;
        this.betweenValue = true;
    }

    protected Criterion(String condition, Object value, Object secondValue) {
        this(condition, value, secondValue, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Criterion criterion = (Criterion) o;
        return noValue == criterion.noValue &&
                singleValue == criterion.singleValue &&
                likeValue == criterion.likeValue &&
                betweenValue == criterion.betweenValue &&
                listValue == criterion.listValue &&
                Objects.equals(condition, criterion.condition) &&
                Objects.equals(value, criterion.value) &&
                Objects.equals(secondValue, criterion.secondValue) &&
                Objects.equals(typeHandler, criterion.typeHandler) &&
                sqlLike == criterion.sqlLike;
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, value, secondValue, noValue, singleValue, likeValue, betweenValue, listValue, typeHandler, sqlLike);
    }
}
