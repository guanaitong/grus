/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentelemetry;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by August.Zhou on 2022/10/11 09:54
 */
@ConfigurationProperties(prefix = "grus.opentelemetry")
public class OpenTelemetryProperties {

    /**
     * feignService传播下游时，采用新的span
     */
    private boolean propagateWithNew = false;

    /**
     * 是否对redis、mysql等不支持tracing的client进行传播
     */
    private boolean propagateUnSupportedMiddleware = false;
    private Collector collector = new Collector();

    public boolean isPropagateWithNew() {
        return propagateWithNew;
    }

    public boolean isPropagateUnSupportedMiddleware() {
        return propagateUnSupportedMiddleware;
    }

    public void setPropagateUnSupportedMiddleware(boolean propagateUnSupportedMiddleware) {
        this.propagateUnSupportedMiddleware = propagateUnSupportedMiddleware;
    }

    public void setPropagateWithNew(boolean propagateWithNew) {
        this.propagateWithNew = propagateWithNew;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public static class Collector {
        /**
         * 是否启用collector
         */
        private boolean enable = false;

        /**
         * grpc远程地址
         */
        private String endpoint;

        /**
         * 采样百分比
         */
        private double ratio = 0.1;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public double getRatio() {
            return ratio;
        }

        public Collector setRatio(double ratio) {
            this.ratio = ratio;
            return this;
        }
    }
}
