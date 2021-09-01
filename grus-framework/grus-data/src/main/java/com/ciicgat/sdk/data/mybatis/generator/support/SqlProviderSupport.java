/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.support;

import com.ciicgat.sdk.data.mybatis.generator.condition.ConditionExample;
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
        return getSqlScript(method, getEntityClassFromProviderContext(providerContext));
    }

    public static String getSqlScript(String method, Class<?> entityClass) {
        return SqlProviderSupport.getSqlScript(method, entityClass, () -> {
            ReflectEntity reflectEntity = ReflectUtils.resolveEntity(entityClass);
            ReflectEntityHelper reflectEntityHelper = reflectEntity.getReflectEntityHelper();
            Map<String, String> paramMap = reflectEntityHelper.getVariableMap();
            String sqlSegment = SqlUtils.getSqlSegmentMap().get(method);
            Objects.requireNonNull(sqlSegment, String.format("sqlSegment named '%s' is not exist", method));
            return SqlUtils.resolveMapperScript(sqlSegment, paramMap);
        });
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
        Assert.isTrue(example.getOredCriteria().size() > 0, "Condition can't be empty");
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
