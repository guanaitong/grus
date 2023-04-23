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

    private Collector collector = new Collector();

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public static class Collector {
        /**
         * zk里的命名空间。一般不用填写，默认使用应用名
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
