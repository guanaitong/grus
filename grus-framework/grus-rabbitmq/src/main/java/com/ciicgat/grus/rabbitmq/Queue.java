/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2022/7/7 16:50
 */
public class Queue {

    /**
     * 队列名
     */
    private String name;

    /**
     * 队列的配置参数
     */
    private Map<String, Object> args = new HashMap<>();
    /**
     * 是否持久化，默认是
     */
    private boolean durable = true;
    /**
     * 是否链接断开时自动删除，默认否
     */
    private boolean exclusive = false;
    /**
     * 是否自动删除，默认否
     */
    private boolean autoDelete = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }
}
