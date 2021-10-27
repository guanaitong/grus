/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import com.ciicgat.sdk.data.mybatis.generator.support.SFunction;
import com.ciicgat.sdk.data.mybatis.generator.util.LambdaUtils;

/**
 * Lambda条件
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class LambdaCriteria<T> extends GeneratedCriteria<T, SFunction<T, ?>, LambdaCriteria<T>> {
    @Override
    protected String columnToString(SFunction<T, ?> column) {
        return LambdaUtils.getFieldName(column);
    }

    public LambdaCriteria eq(String column, Object val) {
        this.addCriterion(this.contactCondition(column, SqlKeyword.EQ), val, column);
        return this;
    }
}
