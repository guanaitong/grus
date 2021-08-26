/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import java.util.Objects;

/**
 * Created by August.Zhou on 2019-03-06 13:41.
 */
public final class GrusService {

    private final String serviceName;

    private final String host;


    public GrusService(String serviceName, String host) {
        this.serviceName = serviceName;
        this.host = host;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getHost() {
        return host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrusService)) return false;
        GrusService that = (GrusService) o;
        return Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, host);
    }
}
