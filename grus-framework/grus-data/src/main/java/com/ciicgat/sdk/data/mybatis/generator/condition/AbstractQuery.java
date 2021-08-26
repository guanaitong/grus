/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import java.util.Objects;

/**
 * 抽象查询
 *
 * @author Clive Yuan
 * @date 2020/10/30
 */
public abstract class AbstractQuery<T> extends ConditionExample<T> implements Query<T> {

    private T entity;

    @Override
    public void setEntity(T entity) {
        Objects.requireNonNull(entity);
        this.entity = entity;
    }

    @Override
    public void clear() {
        super.clear();
        entity = null;
    }

    @Override
    public T getEntity() {
        return entity;
    }
}
