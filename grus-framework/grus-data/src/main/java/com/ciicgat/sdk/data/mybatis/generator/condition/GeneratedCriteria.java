/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import com.ciicgat.sdk.data.mybatis.generator.util.SqlUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 生成的条件集合
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class GeneratedCriteria<T, R, Children> implements Compare<Children, R>, Func<Children, R> {

    /**
     * 占位符
     */
    protected final Children typedThis = (Children) this;

    private List<Criterion> criteria;

    public GeneratedCriteria() {
        this.criteria = new ArrayList<>();
    }

    public boolean isValid() {
        return criteria.size() > 0;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    protected void addCriterion(String condition) {
        if (condition == null) {
            throw new RuntimeException("Value for condition cannot be null");
        }
        criteria.add(new Criterion(condition));
    }

    protected void addCriterion(R column, SqlKeyword sqlKeyword) {
        this.addCriterion(this.contactCondition(this.columnToString(column), sqlKeyword));
    }

    protected void addCriterion(R column, SqlKeyword sqlKeyword, Object value) {
        String columnName = this.columnToString(column);
        this.addCriterion(this.contactCondition(columnName, sqlKeyword), value, columnName);
    }

    protected void addCriterion(R column, SqlKeyword sqlKeyword, Object value1, Object value2) {
        String columnName = this.columnToString(column);
        this.addCriterion(this.contactCondition(columnName, sqlKeyword), value1, value2, columnName);
    }

    protected void addCriterion(String condition, Object value, String property) {
        if (value == null) {
            throw new RuntimeException("Value for " + property + " cannot be null");
        }
        criteria.removeIf(x -> Objects.equals(x.getCondition(), condition));
        criteria.add(new Criterion(condition, value));
    }

    protected void addLikeCriterion(SqlKeyword sqlKeyword, SqlLike sqlLike, Object value, R column) {
        String columnName = this.columnToString(column);
        if (value == null) {
            throw new RuntimeException("Value for " + columnName + " cannot be null");
        }
        criteria.add(new Criterion(this.contactCondition(columnName, sqlKeyword), value, sqlLike));
    }

    protected void addCriterion(String condition, Object value1, Object value2, String property) {
        if (value1 == null || value2 == null) {
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value1, value2));
    }

    public String contactCondition(String columnName, SqlKeyword sqlKeyword) {
        SqlUtils.checkColumnName(columnName);
        return String.format("`%s` %s", columnName, sqlKeyword.getKeyword());
    }

    protected abstract String columnToString(R column);

    @Override
    public Children eq(R column, Object val) {
        addCriterion(column, SqlKeyword.EQ, val);
        return typedThis;
    }

    @Override
    public Children ne(R column, Object val) {
        addCriterion(column, SqlKeyword.NE, val);
        return typedThis;
    }

    @Override
    public Children gt(R column, Object val) {
        addCriterion(column, SqlKeyword.GT, val);
        return typedThis;
    }

    @Override
    public Children ge(R column, Object val) {
        addCriterion(column, SqlKeyword.GE, val);
        return typedThis;
    }

    @Override
    public Children lt(R column, Object val) {
        addCriterion(column, SqlKeyword.LT, val);
        return typedThis;
    }

    @Override
    public Children le(R column, Object val) {
        addCriterion(column, SqlKeyword.LE, val);
        return typedThis;
    }

    @Override
    public Children between(R column, Object val1, Object val2) {
        addCriterion(column, SqlKeyword.BETWEEN, val1, val2);
        return typedThis;
    }

    @Override
    public Children notBetween(R column, Object val1, Object val2) {
        addCriterion(column, SqlKeyword.NOT_BETWEEN, val1, val2);
        return typedThis;
    }

    @Override
    public Children like(R column, Object val) {
        addLikeCriterion(SqlKeyword.LIKE, SqlLike.DEFAULT, val, column);
        return typedThis;
    }

    @Override
    public Children notLike(R column, Object val) {
        addLikeCriterion(SqlKeyword.NOT_LIKE, SqlLike.DEFAULT, val, column);
        return typedThis;
    }

    @Override
    public Children likeLeft(R column, Object val) {
        addLikeCriterion(SqlKeyword.LIKE, SqlLike.LEFT, val, column);
        return typedThis;
    }

    @Override
    public Children likeRight(R column, Object val) {
        addLikeCriterion(SqlKeyword.LIKE, SqlLike.RIGHT, val, column);
        return typedThis;
    }

    @Override
    public Children isNull(R column) {
        addCriterion(column, SqlKeyword.IS_NULL);
        return typedThis;
    }

    @Override
    public Children isNotNull(R column) {
        addCriterion(column, SqlKeyword.IS_NOT_NULL);
        return typedThis;
    }

    @Override
    public Children in(R column, Collection<?> coll) {
        addCriterion(column, SqlKeyword.IN, coll);
        return typedThis;
    }

    @Override
    public Children notIn(R column, Collection<?> coll) {
        addCriterion(column, SqlKeyword.NOT_IN, coll);
        return typedThis;
    }
}
