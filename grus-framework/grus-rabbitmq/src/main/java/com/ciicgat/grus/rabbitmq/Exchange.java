/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2022/7/12 14:11
 */
public class Exchange {
    /**
     * 交换机名字
     */
    private String name;
    /**
     * 类型
     */
    private BuiltinExchangeType type;

    /**
     * 队列的配置参数
     */
    private Map<String, Object> args = new HashMap<>();

    public Exchange() {
    }

    public Exchange(String name, BuiltinExchangeType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BuiltinExchangeType getType() {
        return type;
    }

    public void setType(BuiltinExchangeType type) {
        this.type = type;
    }
    
    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }
}
