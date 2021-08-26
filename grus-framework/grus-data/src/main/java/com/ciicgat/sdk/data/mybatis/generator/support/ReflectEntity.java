/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.support;

import java.io.Serializable;
import java.util.List;

/**
 * 反射实体对象
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class ReflectEntity implements Serializable {

    private String simpleName;
    private String tableName;
    private List<ReflectField> fields;
    private ReflectEntityHelper reflectEntityHelper;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ReflectField> getFields() {
        return fields;
    }

    public void setFields(List<ReflectField> fields) {
        this.fields = fields;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public ReflectEntityHelper getReflectEntityHelper() {
        return reflectEntityHelper;
    }

    public void setReflectEntityHelper(ReflectEntityHelper reflectEntityHelper) {
        this.reflectEntityHelper = reflectEntityHelper;
    }
}
