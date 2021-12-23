/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.core;

/**
 * Created by August.Zhou on 2019-06-26 12:37.
 */
public enum Module {

    REDIS("redis"),
    SERVLET("web-servlet"),
    HTTP_CLIENT("httpclient"),
    FEIGN("feign"),
    DB("db"),
    ELASTICSEARCH("es"),
    RABBITMQ("rabbitmq");

    private final String name;

    Module(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
