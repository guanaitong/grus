/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;


import java.util.Objects;

/**
 * config of gconf
 * Created by August.Zhou on 2018/8/23 10:21.
 */
public class GconfConfig {
    private final String domain;
    private final String appName;
    private final String workIdc;
    private final String workEnv;

    public GconfConfig(String domain, String appName, String workIdc, String workEnv) {
        this.domain = domain;
        this.appName = appName;
        this.workIdc = workIdc;
        this.workEnv = workEnv;
    }

    public String getDomain() {
        return domain;
    }

    public String getAppName() {
        return appName;
    }

    public String getWorkIdc() {
        return workIdc;
    }

    public String getWorkEnv() {
        return workEnv;
    }

    public static final String DOMAIN;

    static {
        String domain = null;
        if (Objects.nonNull(System.getenv("KUBERNETES_SERVICE_HOST"))) {
            domain = "gconf.kube-system";
        } else {
            domain = "gconf." + WorkRegion.getCurrentWorkRegion().getServiceDomainSuffix();
        }

        DOMAIN = domain;
    }
}
