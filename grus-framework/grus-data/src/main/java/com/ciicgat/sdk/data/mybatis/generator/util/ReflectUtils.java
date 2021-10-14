/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.util;

import com.ciicgat.sdk.data.mybatis.generator.annotation.TableField;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableId;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableName;
import com.ciicgat.sdk.data.mybatis.generator.support.ReflectEntity;
import com.ciicgat.sdk.data.mybatis.generator.support.ReflectEntityHelper;
import com.ciicgat.sdk.data.mybatis.generator.support.ReflectField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 反射工具
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class ReflectUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtils.class);
    private static final Map<String, ReflectEntity> REFLECT_ENTITY_CACHE = new ConcurrentHashMap<>();
    private static final String GET_METHOD_PREFIX = "get";
    private static final String PRIMARY_KEY_NAME = "id";
    private static final String JAVA_OBJECT = "java.lang.Object";

    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
        return getClassGenericType(false, clazz, index);
    }

    public static Class<?> getClassGenericType(final boolean isInterface, final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (isInterface) {
            Type[] genericInterfaces = clazz.getGenericInterfaces();
            if (genericInterfaces.length > 0) {
                genType = genericInterfaces[0];
            }
        }
        if (!(genType instanceof ParameterizedType)) {
            LOGGER.warn("Warn: {}'s superclass not ParameterizedType", clazz.getSimpleName());
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            LOGGER.warn("Warn: Index: {}, Size of {}'s Parameterized Type: {} .", index, clazz.getSimpleName(), params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            LOGGER.warn("Warn: {} not set the actual class on superclass generic parameter", clazz.getSimpleName());
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    /**
     * 解析反射实体
     *
     * @param clazz 实体类对象
     * @return
     */
    public static ReflectEntity resolveEntity(Class<?> clazz) {
        String name = clazz.getName();
        return Optional.ofNullable(REFLECT_ENTITY_CACHE.get(name))
                .orElseGet(() -> {
                    ReflectEntity reflectEntity = doResolveEntity(clazz);
                    REFLECT_ENTITY_CACHE.put(name, reflectEntity);
                    return reflectEntity;
                });
    }

    private static ReflectEntity doResolveEntity(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        ReflectEntity reflectEntity = new ReflectEntity();
        String simpleName = clazz.getSimpleName();
        String tableName = simpleName;
        // 读取类
        TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
        if (Objects.nonNull(tableNameAnnotation)) {
            tableName = tableNameAnnotation.value();
        }
        reflectEntity.setTableName(tableName);
        reflectEntity.setSimpleName(simpleName);

        // 读取字段
        List<ReflectField> fields = new ArrayList<>();
        reflectEntity.setFields(fields);
        List<Field> declaredFields = getAllDeclaredFields(clazz);
        for (Field declaredField : declaredFields) {
            String name = declaredField.getName();
            boolean primaryKey = false;
            boolean ignoreSaving = false;
            String fieldName = name;
            TableId tableIdAnnotation = declaredField.getAnnotation(TableId.class);
            if (Objects.nonNull(tableIdAnnotation)) {
                if (StringUtils.isNotBlank(tableIdAnnotation.value())) {
                    fieldName = tableIdAnnotation.value();
                }
                primaryKey = true;
                ignoreSaving = true;
            }

            TableField tableFieldAnnotation = declaredField.getAnnotation(TableField.class);
            if (Objects.nonNull(tableFieldAnnotation)) {
                if (StringUtils.isNotBlank(tableFieldAnnotation.value())) {
                    fieldName = tableFieldAnnotation.value();
                }
                ignoreSaving = tableFieldAnnotation.ignoreSaving();
            }

            ReflectField field = new ReflectField();
            field.setName(name);
            field.setFieldName(fieldName);
            field.setIgnoreSaving(ignoreSaving);
            field.setPrimaryKey(primaryKey);
            fields.add(field);
        }
        reflectEntity.setReflectEntityHelper(ReflectEntityHelper.build(reflectEntity));
        return reflectEntity;
    }


    public static <T> boolean isIdNull(T entity) {
        return isFieldNull(entity, PRIMARY_KEY_NAME);
    }

    public static <T> boolean isFieldNull(T entity, String fieldName) {
        Object value = methodInvoke(getMethod(entity.getClass(), fieldName), entity);
        return Objects.isNull(value);
    }

    /**
     * 获取实体字段和值
     *
     * @param entity 实体对象
     * @param <T>    实体对象类
     * @return
     */
    public static <T> Map<String, Object> resolveEntityFieldAndValue(T entity) {
        Map<String, Object> fieldValueMap = new HashMap<>();
        Class<?> entityClass = entity.getClass();
        List<Field> declaredFields = getAllDeclaredFields(entityClass);
        List<String> fieldNames = declaredFields.stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        Map<String, Field> fieldMap = declaredFields.stream().collect(Collectors.toMap(Field::getName, Function.identity()));

        List<Method> declaredMethods = getAllMethods(entityClass);
        Map<String, Method> getMethodMap = declaredMethods.stream().filter(x -> x.getName().startsWith(GET_METHOD_PREFIX))
                .collect(Collectors.toMap(Method::getName, Function.identity()));

        fieldNames.forEach(fieldName -> {
            String columnName = fieldName;
            Field field = fieldMap.get(fieldName);
            TableField tableField = field.getAnnotation(TableField.class);
            if (Objects.nonNull(tableField) && StringUtils.isNotBlank(tableField.value())) {
                columnName = tableField.value();
            }
            String methodName = GET_METHOD_PREFIX.concat(SqlUtils.firstToUpperCase(fieldName));
            Method method = getMethodMap.get(methodName);
            if (Objects.isNull(method)) {
                return;
            }
            Object value = methodInvoke(method, entity);
            if (Objects.nonNull(value)) {
                fieldValueMap.put(columnName, value);
            }
        });
        return fieldValueMap;
    }

    public static Object methodInvoke(Method method, Object obj, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("methodInvoke error", e);
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(Class<?> entityClass, String fieldName) {
        Method method = getMethodRecursion(entityClass, fieldName);
        if (Objects.isNull(method)) {
            String methodName = GET_METHOD_PREFIX.concat(SqlUtils.firstToUpperCase(fieldName));
            throw new RuntimeException(String.format("NoSuchMethodException: %s.%s()", entityClass, methodName));
        }
        return method;
    }

    /**
     * 递归获取对象get方法
     *
     * @param entityClass 对象类
     * @param fieldName   字段名
     * @return
     */
    private static Method getMethodRecursion(Class<?> entityClass, String fieldName) {
        String methodName = GET_METHOD_PREFIX.concat(SqlUtils.firstToUpperCase(fieldName));
        if (JAVA_OBJECT.equalsIgnoreCase(entityClass.getName())) {
            return null;
        }
        return Arrays.stream(entityClass.getDeclaredMethods())
                .filter(x -> Objects.equals(x.getName(), methodName))
                .findFirst()
                .orElseGet(() -> getMethodRecursion(entityClass.getSuperclass(), fieldName));
    }

    public static List<Method> getAllMethods(Class<?> entityClass) {
        List<Method> list = new ArrayList<>();
        getMethodsRecursion(entityClass, list);
        return list;
    }

    public static void getMethodsRecursion(Class<?> entityClass, List<Method> list) {
        if (JAVA_OBJECT.equalsIgnoreCase(entityClass.getName())) {
            return;
        }
        list.addAll(Arrays.stream(entityClass.getDeclaredMethods()).collect(Collectors.toList()));
        getMethodsRecursion(entityClass.getSuperclass(), list);
    }

    /**
     * 获取当前类声明字段,且非static修饰
     *
     * @param clazz 类对象
     * @return
     */
    public static List<Field> getDeclaredFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        return Arrays.stream(declaredFields).filter(x -> !Modifier.isStatic(x.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有声明字段,且非static修饰 (包括父类)
     *
     * @param clazz 类对象
     * @return
     */
    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        getFieldsRecursion(clazz, list);
        return list;
    }

    /**
     * 递归获取对象字段
     *
     * @param entityClass 对象类
     * @param fields      字段列表
     * @return
     */
    private static void getFieldsRecursion(Class<?> entityClass, List<Field> fields) {
        if (JAVA_OBJECT.equalsIgnoreCase(entityClass.getName())) {
            return;
        }
        Field[] declaredFields = entityClass.getDeclaredFields();
        fields.addAll(Arrays.stream(declaredFields).filter(x -> !Modifier.isStatic(x.getModifiers()))
                .collect(Collectors.toList()));
        getFieldsRecursion(entityClass.getSuperclass(), fields);
    }

}
