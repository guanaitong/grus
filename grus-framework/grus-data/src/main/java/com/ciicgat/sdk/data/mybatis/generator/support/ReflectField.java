/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.support;

import java.io.Serializable;

/**
 * 反射字段
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class ReflectField implements Serializable {

    private String name;
    private String fieldName;
    private Boolean isPrimaryKey;
    private Boolean ignoreSaving;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public Boolean getIgnoreSaving() {
        return ignoreSaving;
    }

    public void setIgnoreSaving(Boolean ignoreSaving) {
        this.ignoreSaving = ignoreSaving;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
