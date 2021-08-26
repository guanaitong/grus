/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.fallback;

import java.util.List;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/3/27 16:04
 * @Description:
 */
public class FallbackConfig {

    public static class ProviderFallback {
        private boolean isFallback;

        // 匹配后就会必定不会被降级端路径，优先级高于include
        private List<String> excludePathList;

        // 匹配后就会被降级端路径
        private List<String> includePathList;

        public boolean isFallback() {
            return isFallback;
        }

        public void setFallback(boolean fallback) {
            isFallback = fallback;
        }

        public List<String> getExcludePathList() {
            return excludePathList;
        }

        public void setExcludePathList(List<String> excludePathList) {
            this.excludePathList = excludePathList;
        }

        public List<String> getIncludePathList() {
            return includePathList;
        }

        public void setIncludePathList(List<String> includePathList) {
            this.includePathList = includePathList;
        }
    }

    public static class ConsumerFallback {

        private String serviceName;

        private boolean isFallback;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public boolean isFallback() {
            return isFallback;
        }

        public void setFallback(boolean fallback) {
            isFallback = fallback;
        }
    }

    private ProviderFallback providerFallback;

    private List<ConsumerFallback> consumerFallbacks;

    public ProviderFallback getProviderFallback() {
        return providerFallback;
    }

    public void setProviderFallback(ProviderFallback providerFallback) {
        this.providerFallback = providerFallback;
    }

    public List<ConsumerFallback> getConsumerFallbacks() {
        return consumerFallbacks;
    }

    public void setConsumerFallbacks(List<ConsumerFallback> consumerFallbacks) {
        this.consumerFallbacks = consumerFallbacks;
    }
}
