/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator;

import com.ciicgat.sdk.data.mybatis.generator.support.SqlProviderSupport;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

import java.util.Map;

/**
 * SQL提供者
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public class SqlProvider implements ProviderMethodResolver {

    public <T> String insert(ProviderContext providerContext, T entity) {
        SqlProviderSupport.checkEntity(entity);
        return SqlProviderSupport.getSqlScript("insert", providerContext);
    }

    public <T> String insertAll(ProviderContext providerContext, T entity) {
        SqlProviderSupport.checkEntity(entity);
        return SqlProviderSupport.getSqlScript("insertAll", providerContext);
    }

    public <T> String insertIgnore(ProviderContext providerContext, T entity) {
        SqlProviderSupport.checkEntity(entity);
        return SqlProviderSupport.getSqlScript("insertIgnore", providerContext);
    }

    public String batchInsert(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkEntityList(paramMap);
        return SqlProviderSupport.getSqlScript("batchInsert", providerContext);
    }

    public <T> String batchInsertIgnore(ProviderContext providerContext, T entity) {
        SqlProviderSupport.checkEntity(entity);
        return SqlProviderSupport.getSqlScript("batchInsertIgnore", providerContext);
    }

    public String delete(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkId(paramMap);
        return SqlProviderSupport.getSqlScript("delete", providerContext);
    }

    public String batchDelete(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkIds(paramMap);
        return SqlProviderSupport.getSqlScript("batchDelete", providerContext);
    }

    public String deleteByExample(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkCondition(paramMap);
        return SqlProviderSupport.getSqlScript("deleteByExample", providerContext);
    }

    public String update(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkEntityMap(paramMap);
        return SqlProviderSupport.getSqlScript("update", providerContext);
    }

    public String updateAll(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkEntityMap(paramMap);
        return SqlProviderSupport.getSqlScript("updateAll", providerContext);
    }

    public String updateByExample(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkCondition(paramMap);
        return SqlProviderSupport.getSqlScript("updateByExample", providerContext);
    }

    public String updateByExampleAll(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkCondition(paramMap);
        return SqlProviderSupport.getSqlScript("updateByExampleAll", providerContext);
    }

    public String get(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkId(paramMap);
        return SqlProviderSupport.getSqlScript("get", providerContext);
    }

    public String batchGet(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.checkIds(paramMap);
        return SqlProviderSupport.getSqlScript("batchGet", providerContext);
    }

    public String getByExample(ProviderContext providerContext) {
        return SqlProviderSupport.getSqlScript("getByExample", providerContext);
    }

    public String list(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.filterCondition(paramMap);
        return SqlProviderSupport.getSqlScript("list", providerContext);
    }

    public String count(ProviderContext providerContext, Map<String, Object> paramMap) {
        SqlProviderSupport.filterCondition(paramMap);
        return SqlProviderSupport.getSqlScript("count", providerContext);
    }

}
