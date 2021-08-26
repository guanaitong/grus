/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

/**
 * @author August
 * @date 2021/7/7 9:33 PM
 */
public class KubernetesAppConfig {
    private String name;
    /**
     * 是否开启
     */
    private boolean on;
    private String scheme = "http";
    private int port;
    /**
     * sh:源IP hash
     * random:随机
     */
    private String scheduler = "random";

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(final boolean on) {
        this.on = on;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getScheduler() {
        return this.scheduler;
    }

    public void setScheduler(final String scheduler) {
        this.scheduler = scheduler;
    }
}
