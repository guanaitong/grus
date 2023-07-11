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

}
