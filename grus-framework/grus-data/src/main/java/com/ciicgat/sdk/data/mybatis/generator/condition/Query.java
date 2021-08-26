/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

/**
 * 查询
 *
 * <p>用于Service层面</p>
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public interface Query<T> extends Conditional<T> {

    /**
     * 设置实体
     *
     * @param entity 实体
     * @return
     */
    void setEntity(T entity);

    /**
     * 获取实体
     *
     * @return
     */
    T getEntity();

}
