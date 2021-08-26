/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

/**
 * 条件集合
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class Criteria<T> extends GeneratedCriteria<T, String, Criteria<T>> {

    @Override
    protected String columnToString(String column) {
        return column;
    }
}
