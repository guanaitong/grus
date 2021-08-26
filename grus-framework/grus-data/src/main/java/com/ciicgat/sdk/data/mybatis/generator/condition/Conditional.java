/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

/**
 * 条件构造器
 *
 * @author Clive Yuan
 * @date 2020/10/30
 */
public interface Conditional<T> {

    /**
     * 创建条件
     *
     * <ul>
     *     <li>如果条件列表为空则默认放入</li>
     *     <li>否则不放入</li>
     * </ul>
     *
     * @return
     */
    Criteria<T> createCriteria();

    /**
     * 创建Lambda条件
     *
     * <ul>
     *     <li>如果条件列表为空则默认放入</li>
     *     <li>否则不放入</li>
     * </ul>
     *
     * @return
     */
    LambdaCriteria<T> createLambdaCriteria();

    /**
     * 设置或者条件
     *
     * @param criteria 条件
     */
    void or(GeneratedCriteria criteria);

    /**
     * 创建或者条件
     * </p>
     * 默认放入条件列表
     *
     * @return
     */
    Criteria<T> or();

    /**
     * 创建Lambda或者条件
     *
     * <ul>
     *     <li>默认放入条件列表</li>
     * </ul>
     *
     * @return
     */
    LambdaCriteria<T> orLambdaCriteria();

    /**
     * 获取首个条件
     *
     * <ul>
     *     <li>如果条件存在则获取</li>
     *     <li>否则创建Criteria并放入</li>
     * </ul>
     *
     * @return 需判断类型是LambdaCriteria还是Criteria
     */
    GeneratedCriteria getExistCriteria();

    /**
     * 清除所有
     */
    void clear();

    /**
     * 设置是否去重
     *
     * @param distinct 是否去重
     */
    void setDistinct(boolean distinct);

    /**
     * offset
     *
     * @param limitStart offset
     */
    void setLimitStart(int limitStart);

    /**
     * limit
     *
     * @param limitEnd limit
     */
    void setLimitEnd(int limitEnd);

    /**
     * 添加排序
     *
     * @param columnName 字段名 (如果字段名不合法将抛出异常)
     * @param isAsc      是否正序
     */
    void addOrderBy(String columnName, boolean isAsc);
}
