/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.template;

import com.ciicgat.sdk.data.mybatis.generator.SqlProvider;
import com.ciicgat.sdk.data.mybatis.generator.condition.Example;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * 基础 Mapper
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public interface BaseMapper<T> {

    boolean USE_GENERATED_KEYS = true;
    String KEY_PROPERTY = "id";

    /**
     * 选择性插入
     * <p>
     * 仅插入非null字段
     *
     * @param entity 实体
     * @return
     */
    @InsertProvider(SqlProvider.class)
    @Options(useGeneratedKeys = USE_GENERATED_KEYS, keyProperty = KEY_PROPERTY)
    int insert(T entity);

    /**
     * 全字段插入
     * <p>
     * 无论是否为null均进行插入
     *
     * @param entity 实体
     * @return
     */
    @InsertProvider(SqlProvider.class)
    @Options(useGeneratedKeys = USE_GENERATED_KEYS, keyProperty = KEY_PROPERTY)
    int insertAll(T entity);

    /**
     * 选择性插入且忽略错误
     * <p>
     * 仅插入非null字段
     *
     * @param entity 实体
     * @return
     */
    @InsertProvider(SqlProvider.class)
    @Options(useGeneratedKeys = USE_GENERATED_KEYS, keyProperty = KEY_PROPERTY)
    int insertIgnore(T entity);

    /**
     * 批量选择性插入
     *
     * @param list 实体列表
     * @return
     */
    @InsertProvider(SqlProvider.class)
    @Options(useGeneratedKeys = USE_GENERATED_KEYS, keyProperty = KEY_PROPERTY)
    int batchInsert(@Param("list") List<T> list);

    /**
     * 批量选择性插入且忽略错误
     *
     * @param list 实体列表
     * @return
     */
    @InsertProvider(SqlProvider.class)
    @Options(useGeneratedKeys = USE_GENERATED_KEYS, keyProperty = KEY_PROPERTY)
    int batchInsertIgnore(@Param("list") List<T> list);

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return
     */
    @DeleteProvider(SqlProvider.class)
    int delete(@Param("id") Long id);

    /**
     * 批量ID删除
     *
     * @param ids ID列表
     * @return
     */
    @DeleteProvider(SqlProvider.class)
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 根据条件删除
     *
     * @param example 条件构造器
     * @return
     */
    @DeleteProvider(SqlProvider.class)
    int deleteByExample(@Param("example") Example<T> example);

    /**
     * 选择性更新
     * <p>
     * 仅更新非null字段
     *
     * @param entity 实体
     * @return
     */
    @UpdateProvider(SqlProvider.class)
    int update(@Param("entity") T entity);

    /**
     * 全字段更新
     * <p>
     * 无论是否为null均进行更新
     *
     * @param entity 实体
     * @return
     */
    @UpdateProvider(SqlProvider.class)
    int updateAll(@Param("entity") T entity);

    /**
     * 根据条件选择性更新
     *
     * @param entity  实体
     * @param example 条件构造器
     * @return
     */
    @UpdateProvider(SqlProvider.class)
    int updateByExample(@Param("entity") T entity, @Param("example") Example<T> example);

    /**
     * 根据条件更新全字段
     *
     * @param entity  实体
     * @param example 条件构造器
     * @return
     */
    @UpdateProvider(SqlProvider.class)
    int updateByExampleAll(@Param("entity") T entity, @Param("example") Example<T> example);

    /**
     * 根据ID获取实体
     *
     * @param id 主键ID
     * @return
     */
    @SelectProvider(SqlProvider.class)
    T get(@Param("id") Long id);

    /**
     * 批量获取实体
     *
     * @param ids ID列表
     * @return
     */
    @SelectProvider(SqlProvider.class)
    List<T> batchGet(@Param("ids") List<Long> ids);

    /**
     * 根据条件获取实体
     * <p>
     * 多个时仅返回ID倒序排序首个
     *
     * @param example 条件构造器
     * @return
     */
    @SelectProvider(SqlProvider.class)
    T getByExample(@Param("example") Example<T> example);

    /**
     * 根据条件列表查询
     *
     * @param example 条件构造器
     * @return
     */
    @SelectProvider(SqlProvider.class)
    List<T> list(@Param("example") Example<T> example);

    /**
     * 根据条件数量查询
     *
     * @param example 条件构造器
     * @return
     */
    @SelectProvider(SqlProvider.class)
    int count(@Param("example") Example<T> example);
}
