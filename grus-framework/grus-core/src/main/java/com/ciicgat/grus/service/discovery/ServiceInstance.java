/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.discovery;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author: August
 * @Date: 2021/7/9 17:58
 */
public class ServiceInstance implements Serializable {
    private final String ip;
    private final int port;

    public ServiceInstance(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final ServiceInstance that = (ServiceInstance) o;
        return this.port == that.port && Objects.equals(this.ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ip, this.port);
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
