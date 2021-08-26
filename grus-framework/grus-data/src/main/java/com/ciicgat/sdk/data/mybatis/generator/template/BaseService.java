/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.template;

import com.ciicgat.sdk.data.mybatis.generator.condition.PageQueryExample;
import com.ciicgat.sdk.data.mybatis.generator.condition.Query;
import com.ciicgat.sdk.lang.convert.Pagination;

import java.util.List;

/**
 * 基础 Service
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public interface BaseService<T> {

    /**
     * 选择性插入
     * <p>
     * 仅插入非null字段
     *
     * @param entity 实体
     * @return
     */
    int insert(T entity);

    /**
     * 全字段插入
     * <p>
     * 无论是否为null均进行插入
     *
     * @param entity 实体
     * @return
     */
    int insertAll(T entity);

    /**
     * 选择性保存
     * <p>entity.id = null  -> 插入</p>
     * <p>entity.id != null -> 更新</p>
     *
     * @param entity 实体
     * @return
     */
    int save(T entity);

    /**
     * 全字段保存
     * <p>entity.id = null  -> 插入</p>
     * <p>entity.id != null -> 更新</p>
     *
     * @param entity 实体
     * @return
     */
    int saveAll(T entity);

    /**
     * 批量选择性插入
     *
     * @param list 实体列表
     * @return
     */
    int batchInsert(List<T> list);

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return
     */
    int delete(Long id);

    /**
     * 批量ID删除
     *
     * @param ids ID列表
     * @return
     */
    int batchDelete(List<Long> ids);

    /**
     * 根据条件删除
     *
     * @return
     */
    int deleteByExample(Query<T> query);

    /**
     * 选择性更新
     * <p>
     * 仅更新非null字段
     *
     * @param entity 实体
     * @return
     */
    int update(T entity);

    /**
     * 全字段更新
     * <p>
     * 无论是否为null均进行更新
     *
     * @param entity 实体
     * @return
     */
    int updateAll(T entity);

    /**
     * 根据条件选择性更新
     *
     * @param entity 实体 (set)
     * @param query  查询条件 (where)
     * @return
     */
    int updateByExample(T entity, Query<T> query);

    /**
     * 根据条件更新全字段
     *
     * @param entity 实体 (set)
     * @param query  查询条件 (where)
     * @return
     */
    int updateByExampleAll(T entity, Query<T> query);

    /**
     * 根据ID获取实体
     *
     * @param id 主键ID
     * @return
     */
    T get(Long id);

    /**
     * 批量获取实体
     *
     * @param ids ID列表
     * @return
     */
    List<T> batchGet(List<Long> ids);

    /**
     * 根据条件获取实体
     * <p>
     * 多个时仅返回ID倒序排序首个
     *
     * @param query 查询条件
     * @return
     */
    T getByExample(Query<T> query);

    /**
     * 根据条件数量查询
     *
     * @param query 查询条件
     * @return
     */
    int count(Query<T> query);

    /**
     * 根据条件列表查询
     *
     * @param query 查询条件
     * @return
     */
    List<T> list(Query<T> query);

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return
     */
    Pagination<T> page(PageQueryExample<T> query);
}
