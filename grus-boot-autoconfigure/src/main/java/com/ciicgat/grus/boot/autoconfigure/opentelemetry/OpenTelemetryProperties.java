/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
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
    private boolean propagateWithNew = true;
    private Collector collector = new Collector();

    public boolean isPropagateWithNew() {
        return propagateWithNew;
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

        private String endpoint;

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
    }
}
