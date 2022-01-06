/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import com.ciicgat.sdk.data.mybatis.generator.util.SqlUtils;
import org.springframework.util.Assert;

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

    protected void addCriterion(R column, SqlKeyword sqlKeyword, Object value) {
        this.addCriterion(this.columnToString(column), value, sqlKeyword);
    }

    protected void addCriterion(String property, Object value, SqlKeyword sqlKeyword) {
        addCriterion(property, value, sqlKeyword, null, false);
    }

    protected void addNoValueCriterion(R column, SqlKeyword sqlKeyword) {
        addCriterion(this.columnToString(column), null, sqlKeyword, null, true);
    }

    protected void addBetweenCriterion(R column, SqlKeyword sqlKeyword, Object value, Object secondValue) {
        Assert.notNull(value, "Between firstValue is required");
        Assert.notNull(secondValue, "Between secondValue is required");
        addCriterion(this.columnToString(column), value, sqlKeyword, secondValue, false);
    }

    protected void addCriterion(String property, Object value, SqlKeyword sqlKeyword, Object secondValue, boolean noValue) {
        criteria.removeIf(x -> Objects.equals(x.getCondition(), SqlUtils.contactCondition(property, sqlKeyword)));
        Criterion criterion;
        if (Objects.nonNull(secondValue)) {
            criterion = Criterion.buildBetween(property, value, secondValue, sqlKeyword);
        } else if (noValue) {
            criterion = Criterion.buildNoValue(property, sqlKeyword);
        } else {
            criterion = Criterion.build(property, value, sqlKeyword);
        }
        criteria.add(criterion);
    }

    protected void addLikeCriterion(SqlKeyword sqlKeyword, SqlLike sqlLike, Object value, R column) {
        criteria.add(Criterion.buildLike(this.columnToString(column), value, sqlLike, sqlKeyword));
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
        addBetweenCriterion(column, SqlKeyword.BETWEEN, val1, val2);
        return typedThis;
    }

    @Override
    public Children notBetween(R column, Object val1, Object val2) {
        addBetweenCriterion(column, SqlKeyword.NOT_BETWEEN, val1, val2);
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
        addNoValueCriterion(column, SqlKeyword.IS_NULL);
        return typedThis;
    }

    @Override
    public Children isNotNull(R column) {
        addNoValueCriterion(column, SqlKeyword.IS_NOT_NULL);
        return typedThis;
    }

    @Override
    public Children isBlank(R column) {
        addCriterion(column, SqlKeyword.EQ, "");
        return typedThis;
    }

    @Override
    public Children isNotBlank(R column) {
        addCriterion(column, SqlKeyword.NE, "");
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
