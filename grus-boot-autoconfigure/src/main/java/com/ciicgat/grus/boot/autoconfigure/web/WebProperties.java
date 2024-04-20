/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by August.Zhou on 2019-05-29 11:23.
 */
@ConfigurationProperties(prefix = "grus.web")
public class WebProperties {
    /**
     * 限制不支持TLSv1.2以下的安全协议
     */
    private boolean disableOldSslProtocol = true;

    /**
     * 错误码前缀，最大值不要超过2146
     */
    private int errorCodePrefix = 1000;

    /**
     * 参数校验异常时, msg 前是否添加 fieldName
     */
    private boolean showFieldNameInError = true;

    /**
     * 是否打印businessException异常堆栈
     */
    private boolean printBusinessErrorStack = false;


    /**
     * http2配置
     */
    private Http2 http2 = new Http2();

    public int getErrorCodePrefix() {
        return errorCodePrefix;
    }

    public void setErrorCodePrefix(int errorCodePrefix) {
        this.errorCodePrefix = errorCodePrefix;
    }

    public boolean isShowFieldNameInError() {
        return showFieldNameInError;
    }

    public void setShowFieldNameInError(boolean showFieldNameInError) {
        this.showFieldNameInError = showFieldNameInError;
    }

    public boolean isPrintBusinessErrorStack() {
        return printBusinessErrorStack;
    }

    public void setPrintBusinessErrorStack(boolean printBusinessErrorStack) {
        this.printBusinessErrorStack = printBusinessErrorStack;
    }

    public boolean isDisableOldSslProtocol() {
        return disableOldSslProtocol;
    }

    public void setDisableOldSslProtocol(boolean disableOldSslProtocol) {
        this.disableOldSslProtocol = disableOldSslProtocol;
    }

    public Http2 getHttp2() {
        return http2;
    }

    public void setHttp2(Http2 http2) {
        this.http2 = http2;
    }


    public static class Http2 {
        /**
         * 是否启用http2
         */
        private boolean enabled;

        /**
         * 是否启用clientAuth，即mtls
         */
        private boolean clientAuth;

        /**
         * http2端口号
         */
        private int port = 8443;


        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isClientAuth() {
            return clientAuth;
        }

        public void setClientAuth(boolean clientAuth) {
            this.clientAuth = clientAuth;
        }
    }
}
