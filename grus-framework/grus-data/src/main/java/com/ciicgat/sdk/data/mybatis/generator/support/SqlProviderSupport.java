/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.support;

import com.ciicgat.sdk.data.mybatis.generator.condition.ConditionExample;
import com.ciicgat.sdk.data.mybatis.generator.condition.Criterion;
import com.ciicgat.sdk.data.mybatis.generator.condition.GeneratedCriteria;
import com.ciicgat.sdk.data.mybatis.generator.util.ReflectUtils;
import com.ciicgat.sdk.data.mybatis.generator.util.SqlUtils;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * SQL提供者支持类
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
@SuppressWarnings("unchecked")
public class SqlProviderSupport {
    private static final Map<String, String> SQL_CACHE = new ConcurrentHashMap<>();

    public static String getSqlScript(String method, ProviderContext providerContext) {
        return doGetSqlScript(method, getEntityClassFromProviderContext(providerContext), null);
    }

    public static String getSqlScript(String method, ProviderContext providerContext, Map<String, String> extendParam) {
        return doGetSqlScript(method, getEntityClassFromProviderContext(providerContext), extendParam);
    }

    public static String doGetSqlScript(String method, Class<?> entityClass, Map<String, String> extendParam) {
        return SqlProviderSupport.getSqlScript(method, entityClass, () -> {
            ReflectEntity reflectEntity = ReflectUtils.resolveEntity(entityClass);
            ReflectEntityHelper reflectEntityHelper = reflectEntity.getReflectEntityHelper();
            Map<String, String> paramMap = reflectEntityHelper.getVariableMap();
            injectExtendParam(paramMap, extendParam);
            String sqlSegment = SqlUtils.getSqlSegmentMap().get(method);
            Objects.requireNonNull(sqlSegment, String.format("sqlSegment named '%s' is not exist", method));
            return SqlUtils.resolveMapperScript(sqlSegment, paramMap);
        });
    }

    private static void injectExtendParam(Map<String, String> paramMap, Map<String, String> extendParam) {
        if (Objects.nonNull(extendParam) && !extendParam.isEmpty()) {
            paramMap.putAll(extendParam);
        }
    }

    public static String getSqlScript(String method, Class<?> clazz, Supplier<String> supplier) {
        ReflectEntity reflectEntity = ReflectUtils.resolveEntity(clazz);
        String tableName = reflectEntity.getTableName();
        String key = tableName.concat(".").concat(method);
        return Optional.ofNullable(SQL_CACHE.get(key)).orElseGet(() -> {
            String sqlScript = supplier.get();
            SQL_CACHE.put(key, sqlScript);
            return sqlScript;
        });
    }

    public static Class<?> getEntityClassFromEntity(Map<String, Object> paramMap) {
        Object entity = paramMap.get("entity");
        Assert.notNull(entity, "entity cant be null");
        return entity.getClass();
    }

    public static Class<?> getEntityClassFromProviderContext(ProviderContext providerContext) {
        return ReflectUtils.getClassGenericType(true, providerContext.getMapperType(), 0);
    }

    public static Class<?> getEntityClassFromMap(Map<String, Object> paramMap) {
        Class<?> entityClass = (Class<?>) paramMap.get("entityClass");
        Assert.notNull(entityClass, "entityClass cant be null");
        return entityClass;
    }

    public static Class<?> getEntityClassFromList(Map<String, Object> paramMap) {
        List list = (List) paramMap.get("list");
        Assert.isTrue(list.size() > 0, "list is empty");
        Object entity = list.get(0);
        Assert.notNull(entity, "entity in list cant be null");
        return entity.getClass();
    }

    /**
     * 检查条件, 避免误操作
     *
     * @param paramMap 参数map
     */
    public static void checkCondition(Map<String, Object> paramMap) {
        ConditionExample example = (ConditionExample) paramMap.get("example");
        Assert.notNull(example, "example cant be null");
        List<GeneratedCriteria> oredCriteria = example.getOredCriteria();
        Assert.isTrue(oredCriteria.size() > 0, "Condition can't be empty");
        for (GeneratedCriteria oredCriterion : oredCriteria) {
            oredCriterion.getCriteria().forEach(x -> {
                Criterion criterion = (Criterion) x;
                if (!criterion.isNoValue()) {
                    Assert.notNull(criterion.getValue(), String.format("Value for '%s' can't be null", criterion.getProperty()));
                }
            });
        }
    }

    /**
     * 过滤条件, 过滤值为空的条件
     *
     * @param paramMap 参数map
     */
    public static void filterCondition(Map<String, Object> paramMap) {
        ConditionExample example = (ConditionExample) paramMap.get("example");
        Assert.notNull(example, "example cant be null");
        List<GeneratedCriteria> oredCriteria = example.getOredCriteria();
        if (oredCriteria.size() == 0) {
            return;
        }
        for (GeneratedCriteria oredCriterion : oredCriteria) {
            oredCriterion.getCriteria().removeIf(x -> {
                Criterion criterion = (Criterion) x;
                return !criterion.isNoValue() && Objects.isNull(criterion.getValue());
            });
        }
    }

    /**
     * 检查id集合
     *
     * @param paramMap 参数map
     */
    public static void checkIds(Map<String, Object> paramMap) {
        List<Long> ids = (List<Long>) paramMap.get("ids");
        Assert.isTrue(Objects.nonNull(ids) && ids.size() > 0, "ids can't be empty");
    }

    /**
     * 检查实体集合
     *
     * @param paramMap 参数map
     */
    public static void checkEntityList(Map<String, Object> paramMap) {
        List list = (List) paramMap.get("list");
        Assert.isTrue(Objects.nonNull(list) && list.size() > 0, "list can't be empty");
    }

    public static void checkEntityMap(Map<String, Object> paramMap) {
        checkEntity(paramMap.get("entity"));
    }

    public static void checkEntity(Object entity) {
        Assert.notNull(entity, "entity can't be empty");
    }

    public static void checkId(Map<String, Object> paramMap) {
        Long id = (Long) paramMap.get("id");
        Assert.notNull(id, "id can't be empty");
    }
}
