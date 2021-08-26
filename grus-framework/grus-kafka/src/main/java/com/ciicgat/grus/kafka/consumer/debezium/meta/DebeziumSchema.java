/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.kafka.consumer.debezium.meta;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wanchongyang
 * @date 2021/5/7 5:39 下午
 */
public class DebeziumSchema implements Serializable {
    private String type;
    private List<Map<String, Object>> fields;
    private Boolean optional;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Map<String, Object>> getFields() {
        return fields;
    }

    public void setFields(List<Map<String, Object>> fields) {
        this.fields = fields;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
