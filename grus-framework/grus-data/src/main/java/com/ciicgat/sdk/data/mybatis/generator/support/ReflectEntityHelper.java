/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.support;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 反射实体帮助类
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class ReflectEntityHelper {

    private final Map<String, String> variableMap = new ConcurrentHashMap<>();
    private final ReflectEntity reflectEntity;

    private ReflectEntityHelper(ReflectEntity reflectEntity) {
        this.reflectEntity = reflectEntity;
    }

    public static ReflectEntityHelper build(ReflectEntity reflectEntity) {
        Objects.requireNonNull(reflectEntity);
        ReflectEntityHelper reflectEntityHelper = new ReflectEntityHelper(reflectEntity);
        reflectEntityHelper.initVariableMap();
        return reflectEntityHelper;
    }

    private void initVariableMap() {
        variableMap.put("tableName", reflectEntity.getTableName());
        variableMap.put("allFieldsString", allFieldsString());
        variableMap.put("fieldsString", fieldsString());
        variableMap.put("fieldsParamString", fieldsParamString());
        variableMap.put("itemFieldsParamString", itemFieldsParamString());
        variableMap.put("conditionalFieldsString", conditionalFieldsString());
        variableMap.put("conditionalFieldsParamString", conditionalFieldsParamString());
        variableMap.put("conditionalSetFieldsString", conditionalSetFieldsString());
        variableMap.put("setFieldsString", setFieldsString());
    }

    public Map<String, String> getVariableMap() {
        return variableMap;
    }

    /**
     * 获取非保存忽略字段
     *
     * @return
     */
    public List<ReflectField> getNotIgnoreSavingFields() {
        return reflectEntity.getFields().stream().filter(x -> !x.getIgnoreSaving()).collect(Collectors.toList());
    }

    /**
     * 获取全部字段, 以逗号分隔
     *
     * @return
     */
    private String allFieldsString() {
        return reflectEntity.getFields().stream().map(x -> String.format("`%s`", x.getFieldName()))
                .collect(Collectors.joining(","));
    }

    private String fieldsString() {
        return this.getNotIgnoreSavingFields().stream().map(x -> String.format("`%s`", x.getFieldName()))
                .collect(Collectors.joining(","));
    }

    private String fieldsParamString() {
        return this.getNotIgnoreSavingFields().stream().map(x -> String.format("#{%s}", x.getName()))
                .collect(Collectors.joining(","));
    }

    private String itemFieldsParamString() {
        return this.getNotIgnoreSavingFields().stream().map(x -> String.format("#{item.%s}", x.getName()))
                .collect(Collectors.joining(","));
    }

    private String conditionalFieldsString() {
        return this.getNotIgnoreSavingFields().stream().map(x -> String.format("<if test=\"%s != null\">`%s`,</if>", x.getName(), x.getFieldName()))
                .collect(Collectors.joining(""));
    }

    private String conditionalFieldsParamString() {
        return this.getNotIgnoreSavingFields().stream().map(x -> String.format("<if test=\"%s != null\">#{%s},</if>", x.getName(), x.getName()))
                .collect(Collectors.joining(""));
    }

    private String conditionalSetFieldsString() {
        return this.getNotIgnoreSavingFields().stream().map(x -> String.format("<if test=\"entity.%s != null\">`%s` = #{entity.%s},</if>", x.getName(), x.getFieldName(), x.getName()))
                .collect(Collectors.joining(""));
    }

    private String setFieldsString() {
        return this.getNotIgnoreSavingFields().stream().map(x -> String.format("`%s` = #{entity.%s}", x.getFieldName(), x.getName()))
                .collect(Collectors.joining(","));
    }


}
